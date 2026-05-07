package com.uit.se356.core.domain.entities.order;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.core.domain.exception.OrderErrorCode;
import com.uit.se356.core.domain.vo.area.ProvinceId;
import com.uit.se356.core.domain.vo.area.WardId;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.order.*;
import java.math.BigDecimal;
import java.util.Objects;

public class Order {
  // ==================== Thuộc tính ====================
  private final OrderId id;
  private String trackingCode;
  private OrderType type;
  private OrderStatus status;

  private UserId customerId;
  private UserId senderId;

  private String senderName;
  private String senderPhone;
  private String senderAddress;
  private WardId senderWardId;
  private ProvinceId senderProvinceId;

  private String recipientName;
  private String recipientPhone;
  private String recipientAddress;
  private WardId recipientWardId;
  private ProvinceId recipientProvinceId;

  private String description;
  private Float weight;
  private Dimensions dimensions;
  private BigDecimal valueDeclared;
  private boolean fragile;
  private boolean requiresSignature;

  private BigDecimal shippingFee;
  private BigDecimal insuranceFee;
  private BigDecimal totalAmount;

  private UserId assignedDriverId;
  private String depotId;
  private String estimatedDeliveryDate;
  private String actualDeliveryDate;

  private String notes;
  private String rejectionReason;

  // ==================== Constructor ====================
  private Order(
      OrderId id,
      String trackingCode,
      OrderType type,
      OrderStatus status,
      UserId customerId,
      UserId senderId,
      String senderName,
      String senderPhone,
      String senderAddress,
      WardId senderWardId,
      ProvinceId senderProvinceId,
      String recipientName,
      String recipientPhone,
      String recipientAddress,
      WardId recipientWardId,
      ProvinceId recipientProvinceId,
      String description,
      Float weight,
      Dimensions dimensions,
      BigDecimal valueDeclared,
      boolean fragile,
      boolean requiresSignature,
      BigDecimal shippingFee,
      BigDecimal insuranceFee,
      BigDecimal totalAmount,
      UserId assignedDriverId,
      String depotId,
      String estimatedDeliveryDate,
      String actualDeliveryDate,
      String notes,
      String rejectionReason) {
    this.id = id;
    this.trackingCode = trackingCode;
    this.type = type;
    this.status = status;
    this.customerId = customerId;
    this.senderId = senderId;
    this.senderName = senderName;
    this.senderPhone = senderPhone;
    this.senderAddress = senderAddress;
    this.senderWardId = senderWardId;
    this.senderProvinceId = senderProvinceId;
    this.recipientName = recipientName;
    this.recipientPhone = recipientPhone;
    this.recipientAddress = recipientAddress;
    this.recipientWardId = recipientWardId;
    this.recipientProvinceId = recipientProvinceId;
    this.description = description;
    this.weight = weight;
    this.dimensions = dimensions;
    this.valueDeclared = valueDeclared;
    this.fragile = fragile;
    this.requiresSignature = requiresSignature;
    this.shippingFee = shippingFee;
    this.insuranceFee = insuranceFee;
    this.totalAmount = totalAmount;
    this.assignedDriverId = assignedDriverId;
    this.depotId = depotId;
    this.estimatedDeliveryDate = estimatedDeliveryDate;
    this.actualDeliveryDate = actualDeliveryDate;
    this.notes = notes;
    this.rejectionReason = rejectionReason;
  }

  // ==================== Factory Methods ====================
  public static Order createNewOrder(
      OrderId id,
      String trackingCode,
      OrderType type,
      UserId customerId,
      UserId senderId,
      String senderName,
      String senderPhone,
      String senderAddress,
      WardId senderWardId,
      ProvinceId senderProvinceId,
      String recipientName,
      String recipientPhone,
      String recipientAddress,
      WardId recipientWardId,
      ProvinceId recipientProvinceId,
      String description,
      Float weight,
      Dimensions dimensions,
      BigDecimal valueDeclared,
      boolean fragile,
      boolean requiresSignature,
      BigDecimal shippingFee,
      BigDecimal insuranceFee) {

    // Validate required fields
    Objects.requireNonNull(id, "Order ID cannot be null");
    Objects.requireNonNull(trackingCode, "Tracking code cannot be null");
    Objects.requireNonNull(type, "Order type cannot be null");
    Objects.requireNonNull(customerId, "Customer ID cannot be null");
    Objects.requireNonNull(senderId, "Sender ID cannot be null");
    Objects.requireNonNull(senderName, "Sender name cannot be null");
    Objects.requireNonNull(senderPhone, "Sender phone cannot be null");
    Objects.requireNonNull(senderAddress, "Sender address cannot be null");
    Objects.requireNonNull(senderWardId, "Sender ward ID cannot be null");
    Objects.requireNonNull(senderProvinceId, "Sender province ID cannot be null");
    Objects.requireNonNull(recipientName, "Recipient name cannot be null");
    Objects.requireNonNull(recipientPhone, "Recipient phone cannot be null");
    Objects.requireNonNull(recipientAddress, "Recipient address cannot be null");
    Objects.requireNonNull(recipientWardId, "Recipient ward ID cannot be null");
    Objects.requireNonNull(recipientProvinceId, "Recipient province ID cannot be null");
    Objects.requireNonNull(weight, "Weight cannot be null");
    Objects.requireNonNull(dimensions, "Dimensions cannot be null");
    Objects.requireNonNull(shippingFee, "Shipping fee cannot be null");

    if (trackingCode.isBlank()) {
      throw new AppException(OrderErrorCode.INVALID_TRACKING_CODE);
    }

    if (weight <= 0) {
      throw new AppException(OrderErrorCode.INVALID_WEIGHT);
    }

    if (shippingFee.compareTo(BigDecimal.ZERO) < 0) {
      throw new AppException(OrderErrorCode.INVALID_SHIPPING_FEE);
    }

    BigDecimal totalFee = calculateTotalFee(shippingFee, insuranceFee);

    return new Order(
        id,
        trackingCode,
        type,
        OrderStatus.PENDING,
        customerId,
        senderId,
        senderName,
        senderPhone,
        senderAddress,
        senderWardId,
        senderProvinceId,
        recipientName,
        recipientPhone,
        recipientAddress,
        recipientWardId,
        recipientProvinceId,
        description,
        weight,
        dimensions,
        valueDeclared,
        fragile,
        requiresSignature,
        shippingFee,
        insuranceFee,
        totalFee,
        null,
        null,
        null,
        null,
        null,
        null);
  }

  public static Order rehydrate(
      OrderId id,
      String trackingCode,
      OrderType type,
      OrderStatus status,
      UserId customerId,
      UserId senderId,
      String senderName,
      String senderPhone,
      String senderAddress,
      WardId senderWardId,
      ProvinceId senderProvinceId,
      String recipientName,
      String recipientPhone,
      String recipientAddress,
      WardId recipientWardId,
      ProvinceId recipientProvinceId,
      String description,
      Float weight,
      Dimensions dimensions,
      BigDecimal valueDeclared,
      boolean fragile,
      boolean requiresSignature,
      BigDecimal shippingFee,
      BigDecimal insuranceFee,
      BigDecimal totalAmount,
      UserId assignedDriverId,
      String depotId,
      String estimatedDeliveryDate,
      String actualDeliveryDate,
      String notes,
      String rejectionReason) {
    return new Order(
        id,
        trackingCode,
        type,
        status,
        customerId,
        senderId,
        senderName,
        senderPhone,
        senderAddress,
        senderWardId,
        senderProvinceId,
        recipientName,
        recipientPhone,
        recipientAddress,
        recipientWardId,
        recipientProvinceId,
        description,
        weight,
        dimensions,
        valueDeclared,
        fragile,
        requiresSignature,
        shippingFee,
        insuranceFee,
        totalAmount,
        assignedDriverId,
        depotId,
        estimatedDeliveryDate,
        actualDeliveryDate,
        notes,
        rejectionReason);
  }

  // ==================== Business Methods ====================
  public void confirmOrder() {
    if (this.status != OrderStatus.PENDING) {
      throw new AppException(OrderErrorCode.INVALID_ORDER_STATUS);
    }
    this.status = OrderStatus.CONFIRMED;
  }

  public void assignDriver(UserId driverId, String depotId) {
    Objects.requireNonNull(driverId, "Driver ID cannot be null");
    Objects.requireNonNull(depotId, "Depot ID cannot be null");
    this.assignedDriverId = driverId;
    this.depotId = depotId;
    this.status = OrderStatus.ASSIGNED;
  }

  public void startDelivery() {
    if (this.status != OrderStatus.ASSIGNED) {
      throw new AppException(OrderErrorCode.INVALID_ORDER_STATUS);
    }
    this.status = OrderStatus.IN_TRANSIT;
  }

  public void deliverOrder(String deliveryDate) {
    Objects.requireNonNull(deliveryDate, "Delivery date cannot be null");
    this.status = OrderStatus.DELIVERED;
    this.actualDeliveryDate = deliveryDate;
  }

  public void rejectOrder(String rejectionReason) {
    Objects.requireNonNull(rejectionReason, "Rejection reason cannot be null");
    if (rejectionReason.isBlank()) {
      throw new AppException(OrderErrorCode.INVALID_REJECTION_REASON);
    }
    this.status = OrderStatus.REJECTED;
    this.rejectionReason = rejectionReason;
  }

  public void cancelOrder() {
    if (this.status == OrderStatus.IN_TRANSIT || this.status == OrderStatus.DELIVERED) {
      throw new AppException(OrderErrorCode.CANNOT_CANCEL_ORDER);
    }
    this.status = OrderStatus.CANCELLED;
  }

  public void updateRecipientInfo(
      String recipientName, String recipientPhone, String recipientAddress) {
    if (this.status != OrderStatus.PENDING && this.status != OrderStatus.CONFIRMED) {
      throw new AppException(OrderErrorCode.CANNOT_UPDATE_RECIPIENT);
    }
    this.recipientName = recipientName;
    this.recipientPhone = recipientPhone;
    this.recipientAddress = recipientAddress;
  }

  // ==================== Helper Methods ====================
  private static BigDecimal calculateTotalFee(BigDecimal shippingFee, BigDecimal insuranceFee) {
    BigDecimal insurance = insuranceFee != null ? insuranceFee : BigDecimal.ZERO;
    return shippingFee.add(insurance);
  }

  // ==================== Getters ====================
  public OrderId getId() {
    return id;
  }

  public String getTrackingCode() {
    return trackingCode;
  }

  public OrderType getType() {
    return type;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public UserId getCustomerId() {
    return customerId;
  }

  public UserId getSenderId() {
    return senderId;
  }

  public String getSenderName() {
    return senderName;
  }

  public String getSenderPhone() {
    return senderPhone;
  }

  public String getSenderAddress() {
    return senderAddress;
  }

  public WardId getSenderWardId() {
    return senderWardId;
  }

  public ProvinceId getSenderProvinceId() {
    return senderProvinceId;
  }

  public String getRecipientName() {
    return recipientName;
  }

  public String getRecipientPhone() {
    return recipientPhone;
  }

  public String getRecipientAddress() {
    return recipientAddress;
  }

  public WardId getRecipientWardId() {
    return recipientWardId;
  }

  public ProvinceId getRecipientProvinceId() {
    return recipientProvinceId;
  }

  public String getDescription() {
    return description;
  }

  public Float getWeight() {
    return weight;
  }

  public Dimensions getDimensions() {
    return dimensions;
  }

  public BigDecimal getValueDeclared() {
    return valueDeclared;
  }

  public boolean isFragile() {
    return fragile;
  }

  public boolean isRequiresSignature() {
    return requiresSignature;
  }

  public BigDecimal getShippingFee() {
    return shippingFee;
  }

  public BigDecimal getInsuranceFee() {
    return insuranceFee;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public UserId getAssignedDriverId() {
    return assignedDriverId;
  }

  public String getDepotId() {
    return depotId;
  }

  public String getEstimatedDeliveryDate() {
    return estimatedDeliveryDate;
  }

  public String getActualDeliveryDate() {
    return actualDeliveryDate;
  }

  public String getNotes() {
    return notes;
  }

  public String getRejectionReason() {
    return rejectionReason;
  }
}
