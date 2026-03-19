package com.uit.se356.core.presentation.rest;

import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.utils.SecurityUtil;
import com.uit.se356.core.domain.vo.authentication.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class DummyController {
  private final SecurityUtil<UserId> securityUtil;

  @HasPermission(
      name = "Dummy Permission",
      description = "Permission to access dummy endpoint",
      resource = "Dummy",
      action = "Access")
  @GetMapping("/dummy")
  public String getDummy() {
    UserId userId = securityUtil.getCurrentUserPrincipal().get().getId();
    return "Dummy response for user: " + userId.value();
  }

  @GetMapping("favicon.ico")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void favicon() {}
}
