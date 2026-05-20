package com.uit.se356.core.application.user.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.user.command.UpdateUserStatusCommand;
import com.uit.se356.core.application.user.port.UserRepository;
import com.uit.se356.core.application.user.result.UserProfileResult;
import com.uit.se356.core.domain.entities.authentication.User;
import com.uit.se356.core.domain.exception.UserErrorCode;

public class UpdateUserStatusHandler
    implements CommandHandler<UpdateUserStatusCommand, UserProfileResult> {

  private final UserRepository userRepository;

  public UpdateUserStatusHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserProfileResult handle(UpdateUserStatusCommand command) {
    User user =
        userRepository
            .findById(command.userId())
            .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

    user.updateStatus(command.status());

    userRepository.update(user);

    return userRepository
        .findProfileById(user.getId())
        .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
  }
}
