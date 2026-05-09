package com.uit.se356.core.application.user.command;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.user.result.UserProfileResult;
import com.uit.se356.core.domain.vo.authentication.RoleId;
import com.uit.se356.core.domain.vo.authentication.UserId;
import java.util.ArrayList;
import java.util.List;

public record UpdateUserRoleCommand(UserId userId, RoleId roleId)
    implements Command<UserProfileResult> {
  public UpdateUserRoleCommand {
    List<FieldError> errors = new ArrayList<>();
    if (userId == null || userId.value().isBlank()) {
      errors.add(
          new FieldError(
              "userId", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"userId"}));
    }
    if (roleId == null || roleId.value().isBlank()) {
      errors.add(
          new FieldError(
              "roleId", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"roleId"}));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
