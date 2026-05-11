package com.uit.se356.core.application.user.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.user.port.UserRepository;
import com.uit.se356.core.application.user.query.FindUserByPhoneQuery;
import com.uit.se356.core.application.user.result.UserProfileResult;
import com.uit.se356.core.domain.exception.UserErrorCode;
import com.uit.se356.core.domain.vo.authentication.PhoneNumber;

public class FindUserByPhoneHandler
    implements QueryHandler<FindUserByPhoneQuery, UserProfileResult> {
  private final UserRepository userRepository;

  public FindUserByPhoneHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserProfileResult handle(FindUserByPhoneQuery query) {
    return userRepository
        .findProfileByPhone(new PhoneNumber(query.phoneNumber()))
        .orElseThrow(
            () ->
                new AppException(
                    UserErrorCode.USER_NOT_FOUND,
                    "User not found with phone: " + query.phoneNumber()));
  }
}
