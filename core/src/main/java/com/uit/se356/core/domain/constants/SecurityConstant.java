package com.uit.se356.core.domain.constants;

public interface SecurityConstant {
  String[] PUBLIC_URLS = {
    "/swagger-ui/**",
    "/v3/api-docs/**",
    "/api/v1/auth/verify-code",
    "/api/v1/auth/send-verification",
    "/api/v1/auth/rotate-token",
    "/api/v1/auth/login",
    "/api/v1/auth/register",
    "/api/v1/auth/reset-password",
    "/api/v1/auth/mfa/challenge",
    "/api/v1/auth/mfa/verify",
    "/api/v1/auth/mfa/recovery",
    "/api/v1/wallet/webhook/**",
  };
  String[] PUBLIC_GET_URLS = {};
}
