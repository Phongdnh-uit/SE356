package com.uit.se356.core.application.wallet.command;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.domain.vo.wallet.PaymentProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record ProcessTopUpWebhookCommand(PaymentProvider provider, Map<String, Object> params)
    implements Command<Void> {
  public ProcessTopUpWebhookCommand {
    List<FieldError> errors = new ArrayList<>();
    if (provider == null) {
      errors.add(
          new FieldError(
              "provider",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"provider"}));
    }
    if (params == null || params.isEmpty()) {
      errors.add(
          new FieldError(
              "params", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"params"}));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
