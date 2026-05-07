package com.uit.se356.core.application.order.result;

import com.uit.se356.core.domain.entities.order.Order;
import com.uit.se356.core.domain.vo.order.OrderStatus;
import com.uit.se356.core.domain.vo.order.OrderType;
import java.math.BigDecimal;

public record OrderResult(
    String id,
    String trackingCode,
    OrderType type,
    OrderStatus status,
    String customerId,
    String senderName,
    String recipientName,
    String recipientAddress,
    BigDecimal totalAmount,
    String assignedDriverId) {
  public static OrderResult fromEntity(Order order) {
    return new OrderResult(
        order.getId().value(),
        order.getTrackingCode(),
        order.getType(),
        order.getStatus(),
        order.getCustomerId().value(),
        order.getSenderName(),
        order.getRecipientName(),
        order.getRecipientAddress(),
        order.getTotalAmount(),
        order.getAssignedDriverId() != null ? order.getAssignedDriverId().value() : null);
  }
}
