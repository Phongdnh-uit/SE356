package com.uit.se356.core.domain.vo.order;

public enum OrderType {
  EXPRESS("Giao hàng nhanh"),
  STANDARD("Giao hàng tiêu chuẩn"),
  ECONOMY("Giao hàng kinh tế");

  private final String displayName;

  OrderType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
