package com.uit.se356.core.domain.vo.vehicle;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.core.domain.exception.VehicleErrorCode;

public record VehicleId(String value) {
  public VehicleId {
    if (value == null || value.isBlank()) {
      throw new AppException(VehicleErrorCode.INVALID_VEHICLE_ID);
    }
  }
}
