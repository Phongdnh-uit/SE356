package com.uit.se356.core.application.area.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.IdGenerator;
import com.uit.se356.core.application.area.command.ImportProvinceGeoJsonCommand;
import com.uit.se356.core.application.area.port.ProvinceRepository;
import com.uit.se356.core.application.area.result.BatchResult;
import com.uit.se356.core.application.area.result.ImportResult;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.entities.area.Province;
import com.uit.se356.core.domain.vo.area.ProvinceId;
import com.uit.se356.core.domain.vo.area.ProvinceType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;

public class ImportProvinceGeoJsonHandler
    implements CommandHandler<ImportProvinceGeoJsonCommand, ImportResult> {

  private final ProvinceRepository provinceRepository;
  private final ObjectMapper objectMapper;
  private final IdGenerator idGenerator;

  public ImportProvinceGeoJsonHandler(
      ProvinceRepository provinceRepository, ObjectMapper objectMapper, IdGenerator idGenerator) {
    this.provinceRepository = provinceRepository;
    this.objectMapper = objectMapper;
    this.idGenerator = idGenerator;
  }

  @HasPermission(
      name = "Import Province GeoJSON",
      description = "Permission to import provinces from GeoJSON file",
      resource = PermissionConstant.Resource.PROVINCE,
      action = PermissionConstant.Action.CREATE)
  @Override
  public ImportResult handle(ImportProvinceGeoJsonCommand command) {
    int imported = 0, skipped = 0, failed = 0;
    List<String> errors = new ArrayList<>();

    try (InputStream inputStream = command.file().getInputStream();
         var parser = objectMapper.createParser(inputStream)) {

      advanceToFeaturesArray(parser);

      List<JsonNode> batch = new ArrayList<>(command.batchSize());

      ObjectReader featureReader = objectMapper.readerFor(JsonNode.class)
          .without(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

      while (parser.nextToken() == JsonToken.START_OBJECT) {
        JsonNode feature = featureReader.readValue(parser);
        batch.add(feature);

        if (batch.size() >= command.batchSize()) {
          BatchResult result = processBatch(batch, errors);
          imported += result.imported();
          skipped += result.skipped();
          failed += result.failed();
          batch.clear();
        }
      }

      // Flush batch còn lại
      if (!batch.isEmpty()) {
        BatchResult result = processBatch(batch, errors);
        imported += result.imported();
        skipped += result.skipped();
        failed += result.failed();
      }

    } catch (AppException e) {
      throw e;
    } catch (Exception e) {
      // Fix: Pass 'e' as the final parameter so the root cause is logged
      throw new AppException(
          CommonErrorCode.UNCATEGORIZED_EXCEPTION,
          e
      );
    }

    return new ImportResult(imported, skipped, failed, errors);
  }

  /**
   * Dùng streaming parser để tìm tới field "features" mà không load toàn bộ JSON vào memory. Chỉ
   * skip qua các field khác ở root level (type, name, crs...).
   */
  private void advanceToFeaturesArray(JsonParser parser) throws Exception {
    while (parser.nextToken() != null) {
      if (parser.currentToken() == JsonToken.PROPERTY_NAME
          && "features".equals(parser.currentName())) {
        parser.nextToken();
        return;
      }
    }
    throw new AppException(
        CommonErrorCode.VALIDATION_ERROR, "Invalid GeoJSON: missing 'features' array");
  }

  /** Xử lý 1 batch: build danh sách Province, bulk insert vào DB. */
  private BatchResult processBatch(List<JsonNode> batch, List<String> errors) {
    int imported = 0, skipped = 0, failed = 0;
    List<Province> toCreate = new ArrayList<>(batch.size());

    for (JsonNode feature : batch) {
      try {
        JsonNode properties = feature.path("properties");

        String name = extractProperty(properties, "ten_tinh", "name", "TinhThanh");
        String code = extractProperty(properties, "ma_tinh", "ISO3166-2", "Ma");
        String loai = extractProperty(properties, "loai");

        if (code.isBlank()) {
          code = extractProperty(properties, "@id", "OBJECTID");
        }

        if (name.isBlank() || code.isBlank()) {
          skipped++;
          continue;
        }

        if (provinceRepository.existsByCode(code)) {
          skipped++;
          continue;
        }

        ProvinceType type = resolveProvinceType(name, loai);
        String newId = idGenerator.generate().toString();
        toCreate.add(Province.create(new ProvinceId(newId), code, name, type));

      } catch (Exception ex) {
        failed++;
        errors.add("Failed to process feature: " + ex.getMessage());
      }
    }

    // Bulk insert
    if (!toCreate.isEmpty()) {
      try {
        provinceRepository.createAll(toCreate);
        imported = toCreate.size();
      } catch (Exception ex) {
        failed += toCreate.size();
        errors.add("Database bulk insert failed for batch: " + ex.getMessage());
      }
    }

    return new BatchResult(imported, skipped, failed);
  }

  private ProvinceType resolveProvinceType(String name, String loai) {
    String normalizedName = name.toLowerCase();
    String normalizedLoai = loai.toLowerCase();

    if (normalizedLoai.contains("thành phố")
        || normalizedName.contains("tp.")
        || normalizedName.contains("tp ")
        || normalizedName.contains("thành phố")) {
      return ProvinceType.CITY;
    }
    return ProvinceType.PROVINCE;
  }

  private String extractProperty(JsonNode properties, String... possibleKeys) {
    for (String key : possibleKeys) {
      if (properties.hasNonNull(key)) {
        return properties.get(key).asText().trim();
      }
    }
    return "";
  }
}
