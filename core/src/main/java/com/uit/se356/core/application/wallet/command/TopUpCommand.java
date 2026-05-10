package com.uit.se356.core.application.wallet.command;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.wallet.PaymentProvider;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record TopUpCommand(UserId userId, BigDecimal amount, PaymentProvider provider)
    implements Command<String> {
  public TopUpCommand {
    List<FieldError> errors = new ArrayList<>();
    if (userId == null) {
      errors.add(
          new FieldError(
              "userId", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"userId"}));
    }
    if (amount == null) {
      errors.add(
          new FieldError(
              "amount", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"amount"}));
    } else if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      errors.add(
          new FieldError(
              "amount", CommonErrorCode.FIELD_INVALID.getMessageKey(), new Object[] {"amount"}));
    }
    if (provider == null) {
      errors.add(
          new FieldError(
              "provider",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"provider"}));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
