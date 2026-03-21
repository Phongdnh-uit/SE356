package com.uit.se356.core.application.authentication.command.role;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.domain.vo.authentication.RoleId;
import com.uit.se356.core.domain.vo.authentication.UserId;
import java.util.ArrayList;
import java.util.List;

public record AssignUserRoleCommand(RoleId roleId, UserId userId) implements Command<Void> {
  public AssignUserRoleCommand {
    List<FieldError> errors = new ArrayList<>();
    if (roleId == null) {
      errors.add(
          new FieldError(
              "roleId", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"roleId"}));
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
