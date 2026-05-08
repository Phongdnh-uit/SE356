package com.uit.se356.core.domain.entities.vehicle;

import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.vehicle.PhysicalCapacity;
import com.uit.se356.core.domain.vo.vehicle.VehicleId;
import com.uit.se356.core.domain.vo.vehicle.VehicleType;
import java.util.Objects;

public class Vehicle {
  private final VehicleId id;
  private String licensePlate;
  private VehicleType type;
  private PhysicalCapacity capacity;
  private UserId shipperId; // Có thể null nếu xe chưa gán cho ai

  private Vehicle(
      VehicleId id,
      String licensePlate,
      VehicleType type,
      PhysicalCapacity capacity,
      UserId shipperId) {
    this.id = id;
    this.licensePlate = licensePlate;
    this.type = type;
    this.capacity = capacity;
    this.shipperId = shipperId;
  }

  // -------- Factory Methods --------
  public static Vehicle create(
      VehicleId id,
      String licensePlate,
      VehicleType type,
      PhysicalCapacity capacity,
      UserId shipperId) {
    Objects.requireNonNull(id);
    Objects.requireNonNull(licensePlate);
    Objects.requireNonNull(type);
    Objects.requireNonNull(capacity);

    return new Vehicle(id, licensePlate, type, capacity, shipperId);
  }

  public void update(
      String licensePlate, VehicleType type, PhysicalCapacity capacity, UserId shipperId) {
    Objects.requireNonNull(licensePlate);
    Objects.requireNonNull(type);
    Objects.requireNonNull(capacity);

    this.licensePlate = licensePlate;
    this.type = type;
    this.capacity = capacity;
    this.shipperId = shipperId;
  }

  public static Vehicle rehydrate(
      VehicleId id,
      String licensePlate,
      VehicleType type,
      PhysicalCapacity capacity,
      UserId shipperId) {
    return new Vehicle(id, licensePlate, type, capacity, shipperId);
  }

  // -------- Getters --------
  public VehicleId getId() {
    return id;
  }

  public String getLicensePlate() {
    return licensePlate;
  }

  public VehicleType getType() {
    return type;
  }

  public PhysicalCapacity getCapacity() {
    return capacity;
  }

  public UserId getShipperId() {
    return shipperId;
  }
}
