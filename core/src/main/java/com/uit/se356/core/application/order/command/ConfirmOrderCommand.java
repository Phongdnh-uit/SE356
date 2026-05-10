package com.uit.se356.core.application.order.command;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.order.result.OrderResult;
import java.util.ArrayList;
import java.util.List;

public record ConfirmOrderCommand(String orderId) implements Command<OrderResult> {
  public ConfirmOrderCommand {
    List<FieldError> errors = new ArrayList<>();
    if (orderId == null || orderId.isBlank()) {
      errors.add(
          new FieldError(
              "orderId", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"orderId"}));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
