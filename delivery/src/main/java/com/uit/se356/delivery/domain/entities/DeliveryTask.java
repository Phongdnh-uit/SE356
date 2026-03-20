package com.uit.se356.delivery.domain.entities;

import com.uit.se356.delivery.domain.vo.Coordinate;
import com.uit.se356.delivery.domain.vo.OrderId;
import com.uit.se356.delivery.domain.vo.PhysicalCapacity;

import java.util.Objects;

public class DeliveryTask {
  private final OrderId id;
  private final Coordinate location;       // Điểm giao đến
  private final PhysicalCapacity demand;   // Khối lượng/Thể tích của đơn hàng này

  private DeliveryTask(OrderId id, Coordinate location, PhysicalCapacity demand) {
    this.id = id;
    this.location = location;
    this.demand = demand;
  }

  // -------- Factory Method --------
  public static DeliveryTask create(OrderId id, Coordinate location, PhysicalCapacity demand) {
    Objects.requireNonNull(id);
    Objects.requireNonNull(location);
    Objects.requireNonNull(demand);

    return new DeliveryTask(id, location, demand);
  }

  // ---------------- Getters ----------------
  public OrderId getId() {
    return id;
  }

  public Coordinate getLocation() {
    return location;
  }

  public PhysicalCapacity getDemand() {
    return demand;
  }
}