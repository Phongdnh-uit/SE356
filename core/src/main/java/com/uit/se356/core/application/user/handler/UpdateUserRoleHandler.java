package com.uit.se356.core.application.user.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.authentication.port.out.RoleRepository;
import com.uit.se356.core.application.user.command.UpdateUserRoleCommand;
import com.uit.se356.core.application.user.port.UserRepository;
import com.uit.se356.core.application.user.result.UserProfileResult;
import com.uit.se356.core.domain.entities.authentication.Role;
import com.uit.se356.core.domain.entities.authentication.User;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import com.uit.se356.core.domain.exception.UserErrorCode;

public class UpdateUserRoleHandler
    implements CommandHandler<UpdateUserRoleCommand, UserProfileResult> {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  public UpdateUserRoleHandler(UserRepository userRepository, RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
  }

  @Override
  public UserProfileResult handle(UpdateUserRoleCommand command) {
    User user =
        userRepository
            .findById(command.userId())
            .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

    Role role =
        roleRepository
            .findById(command.roleId())
            .orElseThrow(() -> new AppException(AuthErrorCode.ROLE_NOT_FOUND));

    user.updateRole(command.roleId());
    userRepository.update(user);

    return userRepository
        .findProfileById(user.getId())
        .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
  }
}
