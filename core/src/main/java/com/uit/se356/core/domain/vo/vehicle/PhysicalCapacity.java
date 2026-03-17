package com.uit.se356.core.domain.vo.vehicle;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.core.domain.exception.VehicleErrorCode;

public record PhysicalCapacity(double maxWeight, double maxVolume) {
  public PhysicalCapacity {
    // BR: Capacities must be greater than zero
    if (maxWeight <= 0 || maxVolume <= 0) {
      throw new AppException(
          VehicleErrorCode.INVALID_CAPACITY,
          "Capacities (Weight and Volume) must be greater than zero.");
    }
  }
}
