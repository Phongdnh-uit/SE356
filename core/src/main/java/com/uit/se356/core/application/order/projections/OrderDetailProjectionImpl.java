package com.uit.se356.core.application.order.projections;

import com.uit.se356.core.domain.vo.order.OrderStatus;
import com.uit.se356.core.domain.vo.order.OrderType;
import java.math.BigDecimal;

public record OrderDetailProjectionImpl(
    String id,
    String trackingCode,
    OrderType type,
    OrderStatus status,
    String customerId,
    String senderId,
    String senderName,
    String senderPhone,
    String senderAddress,
    String senderWardId,
    String senderProvinceId,
    String recipientName,
    String recipientPhone,
    String recipientAddress,
    String recipientWardId,
    String recipientProvinceId,
    String description,
    Float weight,
    String dimensions,
    BigDecimal valueDeclared,
    boolean fragile,
    boolean requiresSignature,
    BigDecimal shippingFee,
    BigDecimal insuranceFee,
    BigDecimal totalAmount,
    String assignedDriverId,
    String depotId,
    String estimatedDeliveryDate,
    String actualDeliveryDate,
    String notes,
    String rejectionReason,
    String createdAt,
    String updatedAt)
    implements OrderDetailProjection {
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
  public String getSenderId() {
    return senderId;
  }

  @Override
  public String getSenderName() {
    return senderName;
  }

  @Override
  public String getSenderPhone() {
    return senderPhone;
  }

  @Override
  public String getSenderAddress() {
    return senderAddress;
  }

  @Override
  public String getSenderWardId() {
    return senderWardId;
  }

  @Override
  public String getSenderProvinceId() {
    return senderProvinceId;
  }

  @Override
  public String getRecipientName() {
    return recipientName;
  }

  @Override
  public String getRecipientPhone() {
    return recipientPhone;
  }

  @Override
  public String getRecipientAddress() {
    return recipientAddress;
  }

  @Override
  public String getRecipientWardId() {
    return recipientWardId;
  }

  @Override
  public String getRecipientProvinceId() {
    return recipientProvinceId;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Float getWeight() {
    return weight;
  }

  @Override
  public String getDimensions() {
    return dimensions;
  }

  @Override
  public BigDecimal getValueDeclared() {
    return valueDeclared;
  }

  @Override
  public boolean isFragile() {
    return fragile;
  }

  @Override
  public boolean isRequiresSignature() {
    return requiresSignature;
  }

  @Override
  public BigDecimal getShippingFee() {
    return shippingFee;
  }

  @Override
  public BigDecimal getInsuranceFee() {
    return insuranceFee;
  }

  @Override
  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  @Override
  public String getAssignedDriverId() {
    return assignedDriverId;
  }

  @Override
  public String getDepotId() {
    return depotId;
  }

  @Override
  public String getEstimatedDeliveryDate() {
    return estimatedDeliveryDate;
  }

  @Override
  public String getActualDeliveryDate() {
    return actualDeliveryDate;
  }

  @Override
  public String getNotes() {
    return notes;
  }

  @Override
  public String getRejectionReason() {
    return rejectionReason;
  }

  @Override
  public String getCreatedAt() {
    return createdAt;
  }

  @Override
  public String getUpdatedAt() {
    return updatedAt;
  }
}
