package com.uit.se356.core.application.area.handler;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.IdGenerator;
import com.uit.se356.core.application.area.command.ImportWardGeoJsonCommand;
import com.uit.se356.core.application.area.port.ProvinceRepository;
import com.uit.se356.core.application.area.port.WardRepository;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.entities.area.Province;
import com.uit.se356.core.domain.entities.area.Ward;
import com.uit.se356.core.domain.vo.area.Polygon;
import com.uit.se356.core.domain.vo.area.WardId;
import com.uit.se356.core.domain.vo.area.WardType;
import com.uit.se356.core.infrastructure.utils.GeoJsonParserUtil;
import java.util.Optional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class ImportWardGeoJsonHandler implements CommandHandler<ImportWardGeoJsonCommand, Integer> {

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
  public Integer handle(ImportWardGeoJsonCommand command) {
    int count = 0;
    JsonFactory factory = new JsonFactory();

    try (JsonParser parser = factory.createParser(command.file().getInputStream())) {
      while (parser.nextToken() != null) {
        if ("features".equals(parser.getCurrentName())) break;
      }

      if (parser.nextToken() != JsonToken.START_ARRAY) {
        throw new AppException(CommonErrorCode.VALIDATION_ERROR, "Không tìm thấy mảng features");
      }

      while (parser.nextToken() == JsonToken.START_OBJECT) {
        try {
          JsonNode feature = objectMapper.readTree(String.valueOf(parser));
          JsonNode properties = feature.path("properties");
          JsonNode geometry = feature.path("geometry");

          if ("Point".equalsIgnoreCase(geometry.path("type").asText(""))) continue;

          String wardCode = properties.path("ma_xa").asText().trim();
          String wardName = properties.path("ten_xa").asText().trim();
          String provinceCode = properties.path("ma_tinh").asText().trim();
          String loai = properties.path("loai").asText().trim(); // "Phường", "Xã", "Thị trấn"

          if (wardCode.isEmpty() || wardName.isEmpty()) continue;

          if (!wardRepository.existsByCode(wardCode)) {
            Optional<Province> provinceOpt = provinceRepository.findByCode(provinceCode);

            if (provinceOpt.isPresent()) {

              // 1. Phân tích Polygon
              Polygon polygon = GeoJsonParserUtil.parsePolygon(geometry);

              // 2. Xác định Loại
              WardType type = WardType.WARD; // Mặc định
              if (loai.equalsIgnoreCase("Phường")) type = WardType.WARD;
              else if (loai.equalsIgnoreCase("Xã")) type = WardType.COMMUNE;
              else if (loai.equalsIgnoreCase("Thị trấn")) type = WardType.TOWNSHIP;

              // 3. Khởi tạo
              String newId = idGenerator.generate().toString();
              Ward ward =
                  Ward.createNewWard(
                      new WardId(newId),
                      wardCode,
                      wardName,
                      provinceOpt.get().getId(), // provinceOpt.get().getId() trả về ProvinceId
                      type,
                      polygon); // Thay null thành polygon vừa parse

              wardRepository.create(ward);
              count++;

            } else {
              System.out.println(
                  "Không tìm thấy tỉnh/thành phố với mã: "
                      + provinceCode
                      + " cho phường/xã: "
                      + wardName);
            }
          }
        } catch (Exception ex) {
          throw new AppException(CommonErrorCode.VALIDATION_ERROR, ex.getMessage());
        }
      }
    } catch (Exception e) {
      throw new AppException(
          CommonErrorCode.UNCATEGORIZED_EXCEPTION, "Lỗi đọc file Streaming: " + e.getMessage());
    }
    return count;
  }
}
