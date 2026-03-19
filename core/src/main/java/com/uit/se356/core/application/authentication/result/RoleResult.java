package com.uit.se356.core.application.authentication.result;

import com.uit.se356.core.domain.entities.authentication.Role;
import com.uit.se356.core.domain.vo.authentication.RoleId;

public record RoleResult(
    RoleId id, String name, String description, boolean isDefault, boolean systemRole) {
  public static RoleResult from(Role role) {
    return new RoleResult(
        role.getId(), role.getName(), role.getDescription(), role.isDefault(), role.isSystemRole());
  }
}
