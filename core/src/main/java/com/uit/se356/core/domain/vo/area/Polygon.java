package com.uit.se356.core.domain.vo.area;

import java.util.List;
import java.util.Objects;

public record Polygon(List<Coordinate> coordinates) {
  public Polygon {
    Objects.requireNonNull(coordinates, "Coordinates must not be null");
    if (coordinates.size() < 3) {
      throw new IllegalArgumentException("Polygon must have at least 3 coordinates");
    }
  }
}
