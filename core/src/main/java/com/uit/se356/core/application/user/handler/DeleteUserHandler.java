package com.uit.se356.core.application.user.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.user.command.DeleteUserCommand;
import com.uit.se356.core.application.user.port.UserRepository;
import com.uit.se356.core.domain.exception.UserErrorCode;

public class DeleteUserHandler implements CommandHandler<DeleteUserCommand, Void> {

  private final UserRepository userRepository;

  public DeleteUserHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Void handle(DeleteUserCommand command) {
    if (!userRepository.existsById(command.userId())) {
      throw new AppException(UserErrorCode.USER_NOT_FOUND);
    }

    userRepository.delete(command.userId());

    return null;
  }
}
