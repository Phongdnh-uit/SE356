package com.uit.se356.core.application.authentication.handler.role;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.IdGenerator;
import com.uit.se356.core.application.authentication.command.role.CreateRoleCommand;
import com.uit.se356.core.application.authentication.port.out.RoleRepository;
import com.uit.se356.core.application.authentication.result.RoleResult;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.entities.authentication.Role;
import com.uit.se356.core.domain.vo.authentication.RoleId;
import java.util.HashMap;
import java.util.Map;

public class CreateRoleHandler implements CommandHandler<CreateRoleCommand, RoleResult> {
  private final RoleRepository roleRepository;
  private final IdGenerator idGenerator;

  public CreateRoleHandler(RoleRepository roleRepository, IdGenerator idGenerator) {
    this.roleRepository = roleRepository;
    this.idGenerator = idGenerator;
  }

  @HasPermission(
      name = "Create Role",
      description = "Permission to create a new role",
      resource = PermissionConstant.Resource.ROLE,
      action = PermissionConstant.Action.CREATE)
  @Override
  public RoleResult handle(CreateRoleCommand command) {
    Map<String, Object> errors = new HashMap<>();
    // 1. Kiểm tra tên role không được trùng
    if (roleRepository.existsByName(command.name())) {
      errors.put("name", "Role name already exists"); // TODO: Hard-code
    }

    // 2. Chỉ cho phép chỉnh mặc định nếu không có role mặc định nào khác
    if (command.isDefault() && !roleRepository.findDefault().isEmpty()) {
      errors.put("isDefault", "A default role already exists"); // TODO: Hard-code
    }

    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }

    Role role =
        Role.create(
            new RoleId(idGenerator.generate().toString()), command.name(), command.description());
    if (command.isDefault()) {
      role.markAsDefault();
    }

    Role newRole = roleRepository.create(role);
    return new RoleResult(
        newRole.getId(),
        newRole.getName(),
        newRole.getDescription(),
        newRole.isDefault(),
        newRole.isSystemRole());
  }
}
