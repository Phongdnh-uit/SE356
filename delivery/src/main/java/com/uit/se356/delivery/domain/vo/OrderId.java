package com.uit.se356.delivery.domain.vo;

public record OrderId(String value) {
  public OrderId {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Order ID cannot be null or blank");
    }
  }
}
