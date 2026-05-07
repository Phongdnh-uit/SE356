package com.uit.se356.core.domain.vo.order;

import java.util.Objects;

public record Dimensions(Double length, Double width, Double height) {
  public Dimensions {
    Objects.requireNonNull(length, "Length cannot be null");
    Objects.requireNonNull(width, "Width cannot be null");
    Objects.requireNonNull(height, "Height cannot be null");

    if (length <= 0 || width <= 0 || height <= 0) {
      throw new IllegalArgumentException("Dimensions must be greater than 0");
    }
  }

  public Double getVolume() {
    return length * width * height;
  }
}
