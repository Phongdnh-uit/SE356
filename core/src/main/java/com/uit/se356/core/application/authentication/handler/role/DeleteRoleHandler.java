package com.uit.se356.core.application.authentication.handler.role;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.authentication.command.role.DeleteRoleCommand;
import com.uit.se356.core.application.authentication.port.out.RoleRepository;
import com.uit.se356.core.application.user.port.UserRepository;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.entities.authentication.Role;
import com.uit.se356.core.domain.entities.authentication.User;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import java.util.List;
import java.util.Optional;

public class DeleteRoleHandler implements CommandHandler<DeleteRoleCommand, Void> {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;

  public DeleteRoleHandler(RoleRepository roleRepository, UserRepository userRepository) {
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
  }

  @HasPermission(
      name = "Delete Role",
      description = "Permission to delete a role",
      resource = PermissionConstant.Resource.ROLE,
      action = PermissionConstant.Action.DELETE)
  @Override
  public Void handle(DeleteRoleCommand command) {
    // 1. Kiểm tra xem vai trò có tồn tại không
    Optional<Role> roleOpt = roleRepository.findById(command.id());
    if (roleOpt.isEmpty()) {
      throw new AppException(AuthErrorCode.ROLE_NOT_FOUND);
    }

    // Chỉ cho phép xóa vai trò không phải là của hệ thống và cũng không được xóa vai trò đang là
    // mặc định
    if (roleOpt.get().isSystemRole() || roleOpt.get().isDefault()) {
      throw new AppException(AuthErrorCode.ROLE_CANNOT_BE_DELETED);
    }

    // 2. Kiểm tra xem có người dùng nào đang sử dụng vai trò này không
    List<User> usersWithRole = userRepository.findByRoleId(command.id());
    if (!usersWithRole.isEmpty()) {
      throw new AppException(AuthErrorCode.ROLE_CANNOT_BE_DELETED);
    }

    // 3. Xóa vai trò
    roleRepository.delete(roleOpt.get());

    return null;
  }
}
