package com.uit.se356.core.application.vehicle.result;

import com.uit.se356.core.domain.entities.vehicle.Vehicle;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.vehicle.VehicleId;
import com.uit.se356.core.domain.vo.vehicle.VehicleType;

public record VehicleResult(
    VehicleId id,
    String licensePlate,
    VehicleType type,
    double maxWeight,
    double maxVolume,
    UserId shipperId) {
  public static VehicleResult fromEntity(Vehicle vehicle) {
    return new VehicleResult(
        vehicle.getId(),
        vehicle.getLicensePlate(),
        vehicle.getType(),
        vehicle.getCapacity().maxWeight(),
        vehicle.getCapacity().maxVolume(),
        vehicle.getShipperId());
  }
}
