package com.uit.se356.core.application.order.projections;

import com.uit.se356.core.domain.vo.order.Dimensions;
import com.uit.se356.core.domain.vo.order.OrderStatus;
import com.uit.se356.core.domain.vo.order.OrderType;
import java.math.BigDecimal;
import java.time.Instant;

public interface OrderDetailProjection {
  String getId();

  String getTrackingCode();

  OrderType getType();

  OrderStatus getStatus();

  String getCustomerId();

  String getSenderId();

  String getSenderName();

  String getSenderPhone();

  String getSenderAddress();

  String getSenderWardId();

  String getSenderProvinceId();

  String getRecipientName();

  String getRecipientPhone();

  String getRecipientAddress();

  String getRecipientWardId();

  String getRecipientProvinceId();

  String getDescription();

  Float getWeight();

  Dimensions getDimensions();

  BigDecimal getValueDeclared();

  boolean isFragile();

  boolean isRequiresSignature();

  BigDecimal getShippingFee();

  BigDecimal getInsuranceFee();

  BigDecimal getTotalAmount();

  String getAssignedDriverId();

  String getDepotId();

  String getEstimatedDeliveryDate();

  String getActualDeliveryDate();

  String getNotes();

  String getRejectionReason();

  Instant getCreatedAt();

  Instant getUpdatedAt();
}
