package com.uit.se356.core.application.internal.handler;

import com.uit.se356.common.dto.PermissionDefinition;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.security.PermissionScanner;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.IdGenerator;
import com.uit.se356.core.application.authentication.port.out.PermissionRepository;
import com.uit.se356.core.application.internal.command.SyncPermissionCommand;
import com.uit.se356.core.domain.entities.authentication.Permission;
import com.uit.se356.core.domain.vo.authentication.PermissionId;
import java.util.List;

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
      resource = "Internal",
      action = "Sync")
  @Override
  public Void handle(SyncPermissionCommand command) {
    // Xóa tất cả quyền hiện có
    // TODO: Hàm deleteAll có thể gây trễ
    permissionRepository.deleteAll();

    // Quét và lưu lại tất cả quyền mới
    // Nếu command có cung cấp package cần quét thì sử dụng, nếu không thì quét toàn bộ
    String packageToScan =
        command.packageName() == null || command.packageName().isBlank()
            ? "com.uit.se356"
            : command.packageName();
    List<PermissionDefinition> permissions = permissionScanner.scan(packageToScan);
    for (PermissionDefinition permission : permissions) {
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
    return null;
  }
}
