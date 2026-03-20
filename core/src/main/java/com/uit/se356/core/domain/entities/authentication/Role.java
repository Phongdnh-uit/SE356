package com.uit.se356.core.domain.entities.authentication;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.core.domain.constants.RoleName;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import com.uit.se356.core.domain.vo.authentication.PermissionId;
import com.uit.se356.core.domain.vo.authentication.RoleId;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Role {
  private final RoleId id;
  private String name;
  private String description;
  private boolean isDefault;
  private final boolean
      systemRole; // Flag để phân biệt role hệ thống (không thể xóa/sửa) và role người dùng
  private Set<PermissionId> permissions = new HashSet<>();

  // ============================ FACTORY ============================
  private Role(
      RoleId id,
      String name,
      String description,
      boolean isDefault,
      boolean systemRole,
      Set<PermissionId> permissions) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.isDefault = isDefault;
    this.systemRole = systemRole;
    this.permissions = permissions;
  }

  public static Role create(RoleId id, String name, String description) {
    Objects.requireNonNull(id);
    Objects.requireNonNull(name);
    return new Role(id, name, description, false, false, new HashSet<>());
  }

  public static Role rehydrate(
      RoleId id,
      String name,
      String description,
      boolean isDefault,
      boolean systemRole,
      Set<PermissionId> permissions) {
    Objects.requireNonNull(id);
    return new Role(id, name, description, isDefault, systemRole, permissions);
  }

  public static Role createSystemRole(RoleId id, String name, String description) {
    Objects.requireNonNull(id);
    Objects.requireNonNull(name);
    return new Role(id, name, description, false, true, new HashSet<>());
  }

  // ============================ BEHAVIORS ============================
  public void update(String name, String description, boolean isDefault) {
    Objects.requireNonNull(name);
    // Chỉ check quyền admin không được sửa
    // Có role user cũng là systemRole nhưng vẫn cho phép sửa vì có thể cần cập nhật mô tả hoặc đổi
    // default
    if (this.systemRole && RoleName.ADMIN.name().equals(name)) {
      throw new AppException(AuthErrorCode.SYSTEM_ROLE_MODIFICATION);
    }
    this.name = name;
    this.description = description;
    this.isDefault = isDefault;
  }

  public void assignPermissions(Set<PermissionId> newPermissions) {
    if (this.systemRole && RoleName.ADMIN.name().equals(this.name)) {
      throw new AppException(AuthErrorCode.SYSTEM_ROLE_MODIFICATION);
    }
    this.permissions = Objects.requireNonNull(newPermissions);
  }

  public void markAsDefault() {
    this.isDefault = true;
  }

  // ============================ GETTERS ============================
  public RoleId getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public boolean isSystemRole() {
    return systemRole;
  }

  public Set<PermissionId> getPermissions() {
    return permissions;
  }
}
