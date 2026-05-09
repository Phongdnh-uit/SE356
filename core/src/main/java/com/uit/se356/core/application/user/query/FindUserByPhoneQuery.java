package com.uit.se356.core.application.user.query;

import com.uit.se356.common.dto.Query;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.core.application.user.result.UserProfileResult;
import com.uit.se356.core.domain.exception.UserErrorCode;

public record FindUserByPhoneQuery(String phoneNumber) implements Query<UserProfileResult> {
  public FindUserByPhoneQuery {
    if (phoneNumber == null || phoneNumber.isBlank()) {
      throw new AppException(UserErrorCode.INVALID_PHONE_FORMAT);
    }
  }
}
