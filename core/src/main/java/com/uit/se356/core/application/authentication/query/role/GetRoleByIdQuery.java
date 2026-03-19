package com.uit.se356.core.application.authentication.query.role;

import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.dto.Query;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.authentication.result.RoleResult;
import com.uit.se356.core.domain.vo.authentication.RoleId;
import java.util.ArrayList;
import java.util.List;

public record GetRoleByIdQuery(RoleId id) implements Query<RoleResult> {
  public GetRoleByIdQuery {
    List<FieldError> errors = new ArrayList<>();
    if (id == null) {
      errors.add(
          new FieldError(
              "id", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"id"}));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
