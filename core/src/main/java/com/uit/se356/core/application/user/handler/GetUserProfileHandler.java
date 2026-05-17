package com.uit.se356.core.application.user.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.user.port.UserRepository;
import com.uit.se356.core.application.user.query.GetUserProfileQuery;
import com.uit.se356.core.application.user.result.UserProfileResult;
import com.uit.se356.core.domain.exception.UserErrorCode;

public class GetUserProfileHandler implements QueryHandler<GetUserProfileQuery, UserProfileResult> {

  private final UserRepository userRepository;

  public GetUserProfileHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserProfileResult handle(GetUserProfileQuery query) {
    return userRepository
        .findProfileById(query.userId())
        .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
  }
}
