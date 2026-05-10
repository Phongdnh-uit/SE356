package com.uit.se356.core.application.order.command;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.order.result.OrderResult;
import java.util.ArrayList;
import java.util.List;

public record UpdateRecipientCommand(
    String orderId, String recipientName, String recipientPhone, String recipientAddress)
    implements Command<OrderResult> {
  public UpdateRecipientCommand {
    List<FieldError> errors = new ArrayList<>();
    if (orderId == null || orderId.isBlank()) {
      errors.add(
          new FieldError(
              "orderId", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"orderId"}));
    }
    if (recipientName == null || recipientName.isBlank()) {
      errors.add(
          new FieldError(
              "recipientName",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"recipientName"}));
    }
    if (recipientPhone == null || recipientPhone.isBlank()) {
      errors.add(
          new FieldError(
              "recipientPhone",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"recipientPhone"}));
    }
    if (recipientAddress == null || recipientAddress.isBlank()) {
      errors.add(
          new FieldError(
              "recipientAddress",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"recipientAddress"}));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
