package com.uit.se356.core.infrastructure.security;

import com.uit.se356.common.security.HasPermission;
import com.uit.se356.core.application.authentication.dto.PermissionCheckerDTO;
import com.uit.se356.core.application.authentication.port.in.PermissionChecker;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {

  private final PermissionChecker permissionChecker;

  @Before("@annotation(hasPermission)")
  public void chec(HasPermission hasPermission) {
    permissionChecker.checkCurrentUserHasPermission(
        new PermissionCheckerDTO(
            hasPermission.resource(), hasPermission.action(), hasPermission.condition()));
  }
}
