package com.uit.se356.core.application.order.projections;

import com.uit.se356.core.domain.vo.order.OrderStatus;
import com.uit.se356.core.domain.vo.order.OrderType;
import java.math.BigDecimal;
import java.time.Instant;

public interface OrderSummaryProjection {
  String getId();

  String getTrackingCode();

  OrderType getType();

  OrderStatus getStatus();

  String getCustomerId();

  String getSenderName();

  String getRecipientName();

  String getRecipientAddress();

  BigDecimal getTotalAmount();

  Instant getCreatedAt();
}
