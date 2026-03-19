package com.uit.se356.core.application.authentication.port.in;

import com.uit.se356.core.application.authentication.dto.PermissionCheckerDTO;

public interface PermissionChecker {
  void checkCurrentUserHasPermission(PermissionCheckerDTO dto);
}
