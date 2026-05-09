package com.uit.se356.core.application.user.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.user.port.UserRepository;
import com.uit.se356.core.application.user.query.FindUserByEmailQuery;
import com.uit.se356.core.application.user.result.UserProfileResult;
import com.uit.se356.core.domain.entities.authentication.User;
import com.uit.se356.core.domain.exception.UserErrorCode;
import com.uit.se356.core.domain.vo.authentication.Email;

public class FindUserByEmailHandler
    implements QueryHandler<FindUserByEmailQuery, UserProfileResult> {
  private final UserRepository userRepository;

  public FindUserByEmailHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserProfileResult handle(FindUserByEmailQuery query) {
    User user =
        userRepository
            .findByEmail(new Email(query.email()))
            .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
    return UserProfileResult.fromUser(user);
  }
}
