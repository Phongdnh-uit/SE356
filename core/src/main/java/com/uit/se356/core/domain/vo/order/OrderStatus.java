package com.uit.se356.core.domain.vo.order;

public enum OrderStatus {
  PENDING("Chờ xác nhận"),
  CONFIRMED("Đã xác nhận"),
  ASSIGNED("Được giao cho tài xế"),
  IN_TRANSIT("Đang giao hàng"),
  DELIVERED("Đã giao hàng"),
  REJECTED("Từ chối"),
  CANCELLED("Đã hủy");

  private final String displayName;

  OrderStatus(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
