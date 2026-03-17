package com.uit.se356.core.infrastructure.utils;

import com.uit.se356.core.domain.vo.area.BoundingBox;
import com.uit.se356.core.domain.vo.area.Coordinate;
import com.uit.se356.core.domain.vo.area.Polygon;
import java.util.ArrayList;
import java.util.List;
import tools.jackson.databind.JsonNode;

public class GeoJsonParserUtil {

  /** Tính toán BoundingBox từ JsonNode Geometry của GeoJSON */
  public static BoundingBox calculateBoundingBox(JsonNode geometryNode) {
    // Mảng lưu trữ: [minLng, maxLng, minLat, maxLat]
    double[] bounds = {Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE};

    JsonNode coordinatesNode = geometryNode.get("coordinates");
    extractBounds(coordinatesNode, bounds);

    // Trả về BoundingBox(minLat, minLng, maxLat, maxLng)
    return new BoundingBox(bounds[2], bounds[0], bounds[3], bounds[1]);
  }

  /** Hàm đệ quy để đào sâu vào các mảng tọa độ */
  private static void extractBounds(JsonNode node, double[] bounds) {
    if (node.isArray()) {
      if (node.get(0).isNumber()) {
        // Cấu trúc của 1 điểm luôn là [Longitude, Latitude] trong GeoJSON
        double lng = node.get(0).asDouble();
        double lat = node.get(1).asDouble();

        if (lng < bounds[0]) bounds[0] = lng; // Cực Tây
        if (lng > bounds[1]) bounds[1] = lng; // Cực Đông
        if (lat < bounds[2]) bounds[2] = lat; // Cực Nam
        if (lat > bounds[3]) bounds[3] = lat; // Cực Bắc
      } else {
        // Đệ quy chui vào mảng con
        for (JsonNode child : node) {
          extractBounds(child, bounds);
        }
      }
    }
  }

  public static Polygon parsePolygon(JsonNode geometryNode) {
    List<Coordinate> coordinates = new ArrayList<>();
    extractCoords(geometryNode.path("coordinates"), coordinates);
    return new Polygon(coordinates);
  }

  private static void extractCoords(JsonNode node, List<Coordinate> coordinates) {
    if (node.isArray() && !node.isEmpty()) {
      if (node.get(0).isNumber()) {
        double lng = node.get(0).asDouble(); // GeoJSON luôn để kinh độ (Lng) trước
        double lat = node.get(1).asDouble(); // Và vĩ độ (Lat) sau
        coordinates.add(new Coordinate(lat, lng));
      } else {
        for (JsonNode child : node) {
          extractCoords(child, coordinates);
        }
      }
    }
  }
}
