package com.uit.se356.core.application.user.command;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.user.result.UserProfileResult;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.authentication.UserStatus;
import java.util.ArrayList;
import java.util.List;

public record UpdateUserStatusCommand(UserId userId, UserStatus status)
    implements Command<UserProfileResult> {
  public UpdateUserStatusCommand {
    List<FieldError> errors = new ArrayList<>();
    if (userId == null || userId.value().isBlank()) {
      errors.add(
          new FieldError(
              "userId", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"userId"}));
    }
    if (status == null) {
      errors.add(
          new FieldError(
              "status", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"status"}));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
