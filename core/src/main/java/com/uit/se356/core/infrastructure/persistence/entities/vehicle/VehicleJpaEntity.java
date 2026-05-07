package com.uit.se356.core.infrastructure.persistence.entities.vehicle;

import com.uit.se356.common.entity.BaseEntity;
import com.uit.se356.core.domain.vo.vehicle.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "vehicles",
    indexes = {@Index(name = "idx_vehicle_shipper", columnList = "shipper_id")})
public class VehicleJpaEntity extends BaseEntity<String> {

  @Column(name = "license_plate", nullable = false, unique = true)
  private String licensePlate;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private VehicleType type;

  // Sức chứa (Dùng cho bài toán Routing)
  @Column(name = "max_weight", nullable = false)
  private Double maxWeight;

  @Column(name = "max_volume", nullable = false)
  private Double maxVolume;

  @Column(name = "shipper_id")
  private String shipperId;
}
