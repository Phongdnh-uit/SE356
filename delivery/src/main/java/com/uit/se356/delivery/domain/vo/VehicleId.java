package com.uit.se356.delivery.domain.vo;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.delivery.domain.exceptions.VehicleErrorCode;

public record VehicleId(String value) {
  public VehicleId {
    if (value == null || value.isBlank()) {
      throw new AppException(VehicleErrorCode.INVALID_VEHICLE_ID);
    }
  }
}
