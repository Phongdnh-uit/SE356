package com.uit.se356.core.application.area.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.IdGenerator;
import com.uit.se356.core.application.area.command.ImportWardGeoJsonCommand;
import com.uit.se356.core.application.area.port.ProvinceRepository;
import com.uit.se356.core.application.area.port.WardRepository;
import com.uit.se356.core.application.area.result.BatchResult;
import com.uit.se356.core.application.area.result.ImportResult;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.entities.area.Province;
import com.uit.se356.core.domain.entities.area.Ward;
import com.uit.se356.core.domain.vo.area.Polygon;
import com.uit.se356.core.domain.vo.area.ProvinceId;
import com.uit.se356.core.domain.vo.area.WardId;
import com.uit.se356.core.domain.vo.area.WardType;
import com.uit.se356.core.infrastructure.utils.GeoJsonParserUtil;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class ImportWardGeoJsonHandler
    implements CommandHandler<ImportWardGeoJsonCommand, ImportResult> {

  private final ProvinceRepository provinceRepository;
  private final WardRepository wardRepository;
  private final ObjectMapper objectMapper;
  private final IdGenerator idGenerator;

  public ImportWardGeoJsonHandler(
      ProvinceRepository provinceRepository,
      WardRepository wardRepository,
      ObjectMapper objectMapper,
      IdGenerator idGenerator) {
    this.provinceRepository = provinceRepository;
    this.wardRepository = wardRepository;
    this.objectMapper = objectMapper;
    this.idGenerator = idGenerator;
  }

  @HasPermission(
      name = "Import Ward GeoJSON",
      description = "Permission to import wards from GeoJSON file",
      resource = PermissionConstant.Resource.WARD,
      action = PermissionConstant.Action.CREATE)
  @Override
  public ImportResult handle(ImportWardGeoJsonCommand command) {
    int imported = 0, skipped = 0, failed = 0;
    List<String> errors = new ArrayList<>();

    // Cache province lookups to minimize DB queries for wards belonging to the same province
    Map<String, Optional<Province>> provinceCache = new ConcurrentHashMap<>();

    try (InputStream inputStream = command.file().getInputStream();
        JsonParser parser = objectMapper.createParser(inputStream)) {

      advanceToFeaturesArray(parser);

      List<JsonNode> batch = new ArrayList<>(command.batchSize());

      while (parser.nextToken() == JsonToken.START_OBJECT) {
        JsonNode feature = objectMapper.readTree(parser);
        batch.add(feature);

        if (batch.size() >= command.batchSize()) {
          BatchResult result = processBatch(batch, provinceCache, errors);
          imported += result.imported();
          skipped += result.skipped();
          failed += result.failed();
          batch.clear();
        }
      }

      // Flush final batch
      if (!batch.isEmpty()) {
        BatchResult result = processBatch(batch, provinceCache, errors);
        imported += result.imported();
        skipped += result.skipped();
        failed += result.failed();
      }

    } catch (AppException e) {
      throw e;
    } catch (Exception e) {
      throw new AppException(
          CommonErrorCode.UNCATEGORIZED_EXCEPTION, "Failed to parse GeoJSON: " + e.getMessage());
    }

    return new ImportResult(imported, skipped, failed, errors);
  }

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

  private BatchResult processBatch(
      List<JsonNode> batch, Map<String, Optional<Province>> provinceCache, List<String> errors) {

    int skipped = 0, failed = 0;
    List<Ward> toCreate = new ArrayList<>(batch.size());

    for (JsonNode feature : batch) {
      try {
        JsonNode properties = feature.path("properties");
        JsonNode geometry = feature.path("geometry");

        String wardCode = properties.path("ma_xa").asString().trim();
        String wardName = properties.path("ten_xa").asString().trim();
        String provinceCode = properties.path("ma_tinh").asString().trim();
        String loai = properties.path("loai").asString().trim();

        if (wardCode.isBlank() || wardName.isBlank()) {
          skipped++;
          continue;
        }

        if (wardRepository.existsByCode(wardCode)) {
          skipped++;
          continue;
        }

        // Check province existence with caching
        Optional<Province> provinceOpt =
            provinceCache.computeIfAbsent(provinceCode, provinceRepository::findByCode);

        if (provinceOpt.isEmpty()) {
          skipped++;
          errors.add("Province not found [code=%s] for ward: %s".formatted(provinceCode, wardName));
          continue;
        }

        Polygon polygon = GeoJsonParserUtil.parsePolygon(geometry);
        WardType type = resolveWardType(loai);
        ProvinceId provinceId = provinceOpt.get().getId();
        String newId = idGenerator.generate().toString();

        toCreate.add(
            Ward.createNewWard(new WardId(newId), wardCode, wardName, provinceId, type, polygon));

      } catch (Exception ex) {
        failed++;
        errors.add("Failed to process feature: " + ex.getMessage());
      }
    }

    if (!toCreate.isEmpty()) {
      wardRepository.createAll(toCreate);
    }

    return new BatchResult(toCreate.size(), skipped, failed);
  }

  private WardType resolveWardType(String loai) {
    return switch (loai) {
      case "Phường" -> WardType.WARD;
      case "Xã" -> WardType.COMMUNE;
      case "Thị trấn" -> WardType.TOWNSHIP;
      default -> WardType.WARD;
    };
  }
}
