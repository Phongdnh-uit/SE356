package com.uit.se356.core.application.vehicle.query;

import com.uit.se356.common.dto.Query;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.vehicle.result.VehicleResult;
import com.uit.se356.core.domain.vo.vehicle.VehicleId;

public record GetVehicleByIdQuery(VehicleId id) implements Query<VehicleResult> {
  public GetVehicleByIdQuery {
    if (id == null || id.value().isBlank()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR);
    }
  }
}
