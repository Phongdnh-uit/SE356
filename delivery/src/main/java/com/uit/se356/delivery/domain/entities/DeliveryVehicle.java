package com.uit.se356.delivery.domain.entities;

import com.uit.se356.delivery.domain.vo.Coordinate;
import com.uit.se356.delivery.domain.vo.PhysicalCapacity;
import com.uit.se356.delivery.domain.vo.VehicleId;

import java.util.Objects;

public class DeliveryVehicle {
  private final VehicleId id;
  private final PhysicalCapacity capacity; // Sức chứa tối đa
  private Coordinate startLocation;        // Tọa độ bắt đầu (Depot của xe)

  private DeliveryVehicle(VehicleId id, PhysicalCapacity capacity, Coordinate startLocation) {
    this.id = id;
    this.capacity = capacity;
    this.startLocation = startLocation;
  }

  // -------- Factory Method --------
  public static DeliveryVehicle create(VehicleId id, PhysicalCapacity capacity, Coordinate startLocation) {
    Objects.requireNonNull(id);
    Objects.requireNonNull(capacity);
    Objects.requireNonNull(startLocation);

    return new DeliveryVehicle(id, capacity, startLocation);
  }

  public void updateStartLocation(Coordinate newStartLocation) {
    Objects.requireNonNull(newStartLocation);
    this.startLocation = newStartLocation;
  }

  // ---------------- Getters ----------------
  public VehicleId getId() {
    return id;
  }

  public PhysicalCapacity getCapacity() {
    return capacity;
  }

  public Coordinate getStartLocation() {
    return startLocation;
  }
}