package com.uit.se356.core.application.user.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.user.command.UpdateUserProfileCommand;
import com.uit.se356.core.application.user.port.UserRepository;
import com.uit.se356.core.application.user.result.UserProfileResult;
import com.uit.se356.core.domain.entities.authentication.User;
import com.uit.se356.core.domain.exception.UserErrorCode;

public class UpdateUserProfileHandler
    implements CommandHandler<UpdateUserProfileCommand, UserProfileResult> {

  private final UserRepository userRepository;

  public UpdateUserProfileHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserProfileResult handle(UpdateUserProfileCommand command) {
    // 1. Fetch User Data (Domain Entity)
    User user =
        userRepository
            .findById(command.userId())
            .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

    // 2. BR: Check for Changes
    boolean isNameUnchanged = command.fullName().equals(user.getFullName());
    if (isNameUnchanged) {
      throw new AppException(
          UserErrorCode.NO_CHANGE_DETECTED); // Đảm bảo bạn có mã lỗi này trong UserErrorCode
    }

    // 3. Update & Save
    user.updateProfile(command.fullName());
    userRepository.update(user);

    // 4. Fetch lại DTO mới nhất để trả về (Bao gồm cả roleName)
    return userRepository
        .findProfileById(user.getId())
        .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
  }
}
