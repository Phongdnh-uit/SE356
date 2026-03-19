package com.uit.se356.core.application.authentication.handler.permission;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.authentication.command.permission.AssignPermissionCommand;
import com.uit.se356.core.application.authentication.port.out.AuthCacheRepository;
import com.uit.se356.core.application.authentication.port.out.PermissionRepository;
import com.uit.se356.core.application.authentication.port.out.RoleRepository;
import com.uit.se356.core.domain.constants.CacheKey;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.constants.RoleName;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import com.uit.se356.core.domain.vo.authentication.PermissionId;
import java.util.Set;

public class AssignPermissionHandler implements CommandHandler<AssignPermissionCommand, Void> {
  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;
  private final AuthCacheRepository authCacheRepository;

  public AssignPermissionHandler(
      RoleRepository roleRepository,
      PermissionRepository permissionRepository,
      AuthCacheRepository authCacheRepository) {
    this.roleRepository = roleRepository;
    this.permissionRepository = permissionRepository;
    this.authCacheRepository = authCacheRepository;
  }

  @HasPermission(
      name = "Assign Permission to Role",
      description = "Permission to assign permissions to a role",
      resource = PermissionConstant.Resource.ROLE,
      action = PermissionConstant.Action.ASSIGN)
  @Override
  public Void handle(AssignPermissionCommand command) {
    // 1. Kiểm tra xem role có tồn tại không
    var role = roleRepository.findById(command.roleId());
    if (role.isEmpty()) {
      throw new AppException(AuthErrorCode.ROLE_NOT_FOUND);
    }
    if (role.get().isSystemRole() && RoleName.ADMIN.name().equals(role.get().getName())) {
      throw new AppException(AuthErrorCode.SYSTEM_ROLE_MODIFICATION);
    }

    // 2. Lấy lên các quyền hạn và gán vào role
    // Note: Tái sử dụng hàm lấy projection để tránh việc load các field không cần thiết, do chỉ cần
    // id để gán quyền hạn vào role
    Set<PermissionId> permissions = permissionRepository.findExistingIds(command.permissionIds());
    if (permissions.size() != command.permissionIds().size()) {
      throw new AppException(AuthErrorCode.PERMISSION_NOT_FOUND);
    }
    // 3. Gán quyền hạn vào role
    role.get().assignPermissions(permissions);

    // 4. Lưu lại role
    roleRepository.update(role.get());

    // 5. Xóa cache liên quan đến role và permission
    String cacheKey = CacheKey.PERMISSION_LIST + ":" + role.get().getId().value();
    authCacheRepository.delete(cacheKey);
    return null;
  }
}
