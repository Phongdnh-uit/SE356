package com.uit.se356.core.application.vehicle.command;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.vehicle.result.VehicleResult;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.vehicle.VehicleId;
import com.uit.se356.core.domain.vo.vehicle.VehicleType;
import java.util.ArrayList;
import java.util.List;

public record SaveVehicleCommand(
    VehicleId id, // Null nếu là Create, có giá trị nếu là Update
    String licensePlate,
    VehicleType type,
    Double maxWeight,
    Double maxVolume,
    UserId shipperId)
    implements Command<VehicleResult> {
  public SaveVehicleCommand {
    List<FieldError> errors = new ArrayList<>();
    if (licensePlate == null || licensePlate.isBlank()) {
      errors.add(
          new FieldError(
              "licensePlate",
              CommonErrorCode.FIELD_INVALID.getMessageKey(),
              new Object[] {"licensePlate"}));
    }
    if (maxWeight == null || maxVolume == null) {
      errors.add(
          new FieldError(
              "capacity",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"capacity"}));
    }
    if (type == null) type = VehicleType.MOTORBIKE; // Default to MOTORBIKE theo SRS

    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
