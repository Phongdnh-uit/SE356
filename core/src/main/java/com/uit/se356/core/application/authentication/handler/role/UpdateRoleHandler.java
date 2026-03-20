package com.uit.se356.core.application.authentication.handler.role;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.authentication.command.role.UpdateRoleCommand;
import com.uit.se356.core.application.authentication.port.out.RoleRepository;
import com.uit.se356.core.application.authentication.result.RoleResult;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.entities.authentication.Role;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import com.uit.se356.core.domain.vo.authentication.RoleId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UpdateRoleHandler implements CommandHandler<UpdateRoleCommand, RoleResult> {
  private final RoleRepository roleRepository;

  public UpdateRoleHandler(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @HasPermission(
      name = "Update Role",
      description = "Permission to update a role",
      resource = PermissionConstant.Resource.ROLE,
      action = PermissionConstant.Action.UPDATE)
  @Override
  public RoleResult handle(UpdateRoleCommand command) {
    // 1. Kiểm tra xem role có tồn tại không
    RoleId roleId = new RoleId(command.id());

    Optional<Role> existing = roleRepository.findById(roleId);
    if (existing.isEmpty()) {
      throw new AppException(AuthErrorCode.ROLE_NOT_FOUND);
    }
    Map<String, Object> errors = new HashMap<>();

    // 2. Kiểm tra xem tên role có bị trùng không (nếu tên được cập nhật)
    roleRepository
        .findByName(command.name())
        .ifPresent(
            role -> {
              if (!role.getId().equals(roleId)) {
                errors.put("name", "Role name already exists");
              }
            });
    // 3. Kiểm tra xem đã có role mặc định nào khác chưa
    roleRepository
        .findDefault()
        .ifPresent(
            defaultRole -> {
              if (command.isDefault() && !defaultRole.getId().equals(roleId)) {
                errors.put("isDefault", "Another default role already exists");
              }
            });

    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }

    Role role = existing.get();

    // 4. Cập nhật thông tin role
    role.update(command.name(), command.description(), command.isDefault());

    // 5. Lưu role đã cập nhật vào repository
    roleRepository.update(role);

    return new RoleResult(
        role.getId(), role.getName(), role.getDescription(), role.isDefault(), role.isSystemRole());
  }
}
