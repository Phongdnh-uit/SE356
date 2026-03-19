package com.uit.se356.core.application.internal.handler;

import com.uit.se356.common.dto.PermissionDefinition;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.security.PermissionScanner;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.IdGenerator;
import com.uit.se356.core.application.authentication.port.out.PermissionRepository;
import com.uit.se356.core.application.internal.command.SyncPermissionCommand;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.entities.authentication.Permission;
import com.uit.se356.core.domain.vo.authentication.PermissionId;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SyncPermissionHandler implements CommandHandler<SyncPermissionCommand, Void> {

  private final PermissionScanner permissionScanner;
  private final PermissionRepository permissionRepository;
  private final IdGenerator idGenerator;

  public SyncPermissionHandler(
      PermissionScanner permissionScanner,
      PermissionRepository permissionRepository,
      IdGenerator idGenerator) {
    this.permissionScanner = permissionScanner;
    this.permissionRepository = permissionRepository;
    this.idGenerator = idGenerator;
  }

  @HasPermission(
      name = "Sync Permissions",
      description = "Sync permissions from code annotations",
      resource = PermissionConstant.Resource.INTERNAL,
      action = PermissionConstant.Action.SYNC)
  @Override
  public Void handle(SyncPermissionCommand command) {
    List<Permission> existingPermissions = permissionRepository.findAll();
    Map<String, Permission> existingPermissionMap =
        existingPermissions.stream()
            .collect(
                Collectors.toMap(
                    perm -> perm.getResource() + ":" + perm.getAction(), perm -> perm));
    Set<String> scannedPermissionKeys = new HashSet<>();

    // Quét và lưu lại tất cả quyền mới
    // Nếu command có cung cấp package cần quét thì sử dụng, nếu không thì quét toàn bộ
    String packageToScan =
        command.packageName() == null || command.packageName().isBlank()
            ? "com.uit.se356"
            : command.packageName();
    List<PermissionDefinition> permissions = permissionScanner.scan(packageToScan);
    for (PermissionDefinition permission : permissions) {
      Permission existingPermission =
          existingPermissionMap.get(permission.resource() + ":" + permission.action());

      if (existingPermission != null) {
        scannedPermissionKeys.add(
            existingPermission.getResource() + ":" + existingPermission.getAction());
        // Nếu quyền đã tồn tại, cập nhật thông tin nếu có thay đổi
        if (!existingPermission.getName().equals(permission.name())
            || !existingPermission.getDescription().equals(permission.description())) {
          existingPermission.update(
              permission.name(), permission.description(), permission.condition());
          permissionRepository.update(existingPermission);
        }
      } else {
        Permission perm =
            Permission.create(
                new PermissionId(idGenerator.generate().toString()),
                permission.name(),
                permission.description(),
                permission.resource(),
                permission.action(),
                permission.condition());
        permissionRepository.create(perm);
      }
    }

    Set<PermissionId> keysToDelete = new HashSet<>();
    // Xóa các quyền đã tồn tại nhưng không còn được quét thấy nữa
    for (Permission existingPermission : existingPermissions) {
      String key = existingPermission.getResource() + ":" + existingPermission.getAction();
      if (!scannedPermissionKeys.contains(key)) {
        keysToDelete.add(existingPermission.getId());
      }
    }

    permissionRepository.deleteAllById(keysToDelete);
    return null;
  }
}
