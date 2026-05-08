package com.uit.se356.core.application.vehicle.command;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.domain.vo.vehicle.VehicleId;
import java.util.ArrayList;
import java.util.List;

public record DeleteVehicleCommand(VehicleId id) implements Command<Void> {
  public DeleteVehicleCommand {
    List<FieldError> errors = new ArrayList<>();
    if (id == null || id.value().isBlank()) {
      errors.add(
          new FieldError(
              "id", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"id"}));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
