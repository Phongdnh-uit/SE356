package com.uit.se356.core.application.authentication.handler.role;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.authentication.command.role.AssignUserRoleCommand;
import com.uit.se356.core.application.authentication.port.out.RoleRepository;
import com.uit.se356.core.application.user.port.UserRepository;
import com.uit.se356.core.domain.constants.PermissionConstant.Action;
import com.uit.se356.core.domain.constants.PermissionConstant.Resource;
import com.uit.se356.core.domain.constants.RoleName;
import com.uit.se356.core.domain.entities.authentication.Role;
import com.uit.se356.core.domain.entities.authentication.User;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import com.uit.se356.core.domain.exception.UserErrorCode;

public class AssignUserRoleHandler implements CommandHandler<AssignUserRoleCommand, Void> {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  public AssignUserRoleHandler(UserRepository userRepository, RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
  }

  @Override
  @HasPermission(
      name = "Assign User Role",
      description =
          "Allows assigning a role to a user, granting them the permissions associated with that"
              + " role.",
      resource = Resource.ROLE,
      action = Action.ASSIGN)
  public Void handle(AssignUserRoleCommand command) {
    // Không được gán quyền ADMIN cho bất kỳ người dùng nào khác ngoài tài khoản ADMIN
    User user =
        userRepository
            .findById(command.userId())
            .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
    Role currentRole =
        roleRepository
            .findById(user.getRoleId())
            .orElseThrow(() -> new AppException(AuthErrorCode.ROLE_NOT_FOUND));

    if (currentRole.isSystemRole() && currentRole.getName().equals(RoleName.ADMIN.name())) {
      throw new AppException(AuthErrorCode.CANNOT_ASSIGN_ADMIN_ROLE);
    }

    if (!roleRepository.existsById(command.roleId())) {
      throw new AppException(AuthErrorCode.ROLE_NOT_FOUND);
    }

    user.updateRole(command.roleId());

    userRepository.update(user);

    return null;
  }
}
