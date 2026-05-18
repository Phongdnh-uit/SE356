package com.uit.se356.core.application.order.command;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.domain.vo.authentication.UserId;
import java.util.ArrayList;
import java.util.List;

public record PayOrderCommand(String orderId, UserId userId) implements Command<Void> {
  public PayOrderCommand {
    List<FieldError> errors = new ArrayList<>();
    if (orderId == null || orderId.isBlank()) {
      errors.add(
          new FieldError(
              "orderId", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"orderId"}));
    }
    if (userId == null) {
      errors.add(
          new FieldError(
              "userId", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"userId"}));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
