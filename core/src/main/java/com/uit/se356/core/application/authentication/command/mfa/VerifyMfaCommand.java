package com.uit.se356.core.application.authentication.command.mfa;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.authentication.result.LoginResult;
import java.util.ArrayList;
import java.util.List;

public record VerifyMfaCommand(String challengeId, String credential)
    implements Command<LoginResult> {
  public VerifyMfaCommand {
    List<FieldError> errors = new ArrayList<>();
    if (challengeId == null || challengeId.isBlank()) {
      errors.add(
          new FieldError(
              "challengeId",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"challengeId"}));
    }
    if (credential == null || credential.isBlank()) {
      errors.add(
          new FieldError(
              "credential",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"credential"}));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
