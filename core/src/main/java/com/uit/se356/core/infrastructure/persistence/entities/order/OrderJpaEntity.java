package com.uit.se356.core.infrastructure.persistence.entities.order;

import com.uit.se356.common.entity.BaseEntity;
import com.uit.se356.core.domain.vo.order.Dimensions;
import com.uit.se356.core.domain.vo.order.OrderStatus;
import com.uit.se356.core.domain.vo.order.OrderType;
import com.uit.se356.core.infrastructure.persistence.entities.area.ProvinceJpaEntity;
import com.uit.se356.core.infrastructure.persistence.entities.area.WardJpaEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
    name = "orders",
    indexes = {
      @Index(name = "idx_orders_customer_id", columnList = "customer_id"),
      @Index(name = "idx_orders_sender_id", columnList = "sender_id"),
      @Index(name = "idx_orders_status", columnList = "status"),
      @Index(name = "idx_orders_type", columnList = "type"),
      @Index(name = "idx_orders_created_at", columnList = "created_at"),
      @Index(name = "idx_orders_tracking_code", columnList = "tracking_code")
    })
@Getter
@Setter
public class OrderJpaEntity extends BaseEntity<String> {

  // ==================== Cơ bản thông tin ====================
  @Column(name = "tracking_code", nullable = false, unique = true, length = 50)
  private String trackingCode;

  @Column(name = "order_type", nullable = false, length = 50)
  @Enumerated(EnumType.STRING)
  private OrderType type;

  @Column(name = "status", nullable = false, length = 50)
  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  // ==================== Thông tin người dùng ====================
  @Column(name = "customer_id", nullable = false, length = 36)
  private String customerId;

  @Column(name = "sender_id", nullable = false, length = 36)
  private String senderId;

  // ==================== Thông tin địa chỉ ====================
  @Column(name = "sender_name", nullable = false, length = 255)
  private String senderName;

  @Column(name = "sender_phone", nullable = false, length = 20)
  private String senderPhone;

  @Column(name = "sender_address", nullable = false, columnDefinition = "TEXT")
  private String senderAddress;

  @Column(name = "sender_ward_id", nullable = false, length = 36)
  private String senderWardId;

  @Column(name = "sender_province_id", nullable = false, length = 36)
  private String senderProvinceId;

  @Column(name = "recipient_name", nullable = false, length = 255)
  private String recipientName;

  @Column(name = "recipient_phone", nullable = false, length = 20)
  private String recipientPhone;

  @Column(name = "recipient_address", nullable = false, columnDefinition = "TEXT")
  private String recipientAddress;

  @Column(name = "recipient_ward_id", nullable = false, length = 36)
  private String recipientWardId;

  @Column(name = "recipient_province_id", nullable = false, length = 36)
  private String recipientProvinceId;

  // ==================== Thông tin gói hàng ====================
  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "weight", nullable = false)
  private Float weight;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "dimensions", columnDefinition = "jsonb")
  private Dimensions dimensions; // JSON: {length, width, height}

  @Column(name = "value_declared", precision = 19, scale = 2)
  private BigDecimal valueDeclared;

  @Column(name = "fragile", nullable = false)
  private boolean fragile;

  @Column(name = "requires_signature", nullable = false)
  private boolean requiresSignature;

  // ==================== Thông tin giá ====================
  @Column(name = "shipping_fee", nullable = false, precision = 19, scale = 2)
  private BigDecimal shippingFee;

  @Column(name = "insurance_fee", nullable = false, precision = 19, scale = 2)
  private BigDecimal insuranceFee;

  @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal totalAmount;

  // ==================== Thông tin giao hàng ====================
  @Column(name = "assigned_driver_id", length = 36)
  private String assignedDriverId;

  @Column(name = "depot_id", length = 36)
  private String depotId;

  @Column(name = "estimated_delivery_date")
  private String estimatedDeliveryDate;

  @Column(name = "actual_delivery_date")
  private String actualDeliveryDate;

  // ==================== Ghi chú ====================
  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @Column(name = "rejection_reason", columnDefinition = "TEXT")
  private String rejectionReason;

  // ==================== Relationships ====================
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "sender_province_id",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private ProvinceJpaEntity senderProvince;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "sender_ward_id",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private WardJpaEntity senderWard;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "recipient_province_id",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private ProvinceJpaEntity recipientProvince;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "recipient_ward_id",
      referencedColumnName = "id",
      insertable = false,
      updatable = false)
  private WardJpaEntity recipientWard;
}
