package com.uit.se356.core.application.depot.command;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.depot.result.DepotResult;
import com.uit.se356.core.domain.exception.DepotErrorCode;
import com.uit.se356.core.domain.vo.depot.DepotId;
import com.uit.se356.core.domain.vo.depot.DepotType;
import java.util.ArrayList;
import java.util.List;

public record UpdateDepotCommand(DepotId id, String name, DepotType type, Double lat, Double lng)
    implements Command<DepotResult> {
  public UpdateDepotCommand {
    List<FieldError> errors = new ArrayList<>();
    if (id == null || id.value().isBlank()) {
      errors.add(
          new FieldError(
              "id", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"id"}));
    }
    if (name == null || name.isBlank()) {
      errors.add(
          new FieldError(
              "name", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"name"}));
    }
    if (type == null) {
      errors.add(
          new FieldError(
              "type", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"type"}));
    }

    if (lat == null) {
      errors.add(
          new FieldError(
              "lat", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"lat"}));
    } else if (lat < -90 || lat > 90) {
      errors.add(
          new FieldError(
              "lat", DepotErrorCode.INVALID_COORDINATE.getMessageKey(), new Object[] {"lat"}));
    }

    if (lng == null) {
      errors.add(
          new FieldError(
              "lng", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"lng"}));
    } else if (lng < -180 || lng > 180) {
      errors.add(
          new FieldError(
              "lat", DepotErrorCode.INVALID_COORDINATE.getMessageKey(), new Object[] {"lat"}));
    }

    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
