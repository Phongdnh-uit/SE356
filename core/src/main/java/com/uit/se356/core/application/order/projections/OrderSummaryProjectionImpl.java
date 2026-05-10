package com.uit.se356.core.application.order.projections;

import com.uit.se356.core.domain.vo.order.OrderStatus;
import com.uit.se356.core.domain.vo.order.OrderType;
import java.math.BigDecimal;

public record OrderSummaryProjectionImpl(
    String id,
    String trackingCode,
    OrderType type,
    OrderStatus status,
    String customerId,
    String senderName,
    String recipientName,
    String recipientAddress,
    BigDecimal totalAmount,
    String createdAt)
    implements OrderSummaryProjection {
  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getTrackingCode() {
    return trackingCode;
  }

  @Override
  public OrderType getType() {
    return type;
  }

  @Override
  public OrderStatus getStatus() {
    return status;
  }

  @Override
  public String getCustomerId() {
    return customerId;
  }

  @Override
  public String getSenderName() {
    return senderName;
  }

  @Override
  public String getRecipientName() {
    return recipientName;
  }

  @Override
  public String getRecipientAddress() {
    return recipientAddress;
  }

  @Override
  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  @Override
  public String getCreatedAt() {
    return createdAt;
  }
}
