package com.uit.se356.core.application.user.query;

import com.uit.se356.common.dto.Query;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.core.application.user.result.UserProfileResult;
import com.uit.se356.core.domain.exception.UserErrorCode;

public record FindUserByEmailQuery(String email) implements Query<UserProfileResult> {
  public FindUserByEmailQuery {
    if (email == null || email.isBlank()) {
      throw new AppException(UserErrorCode.INVALID_EMAIL_FORMAT);
    }
  }
}
