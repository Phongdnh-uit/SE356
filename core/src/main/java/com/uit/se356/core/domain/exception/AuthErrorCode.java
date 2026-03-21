package com.uit.se356.core.domain.exception;

import com.uit.se356.common.exception.ErrorCode;

public enum AuthErrorCode implements ErrorCode {
  INVALID_CREDENTIALS("AUTH_001", "error.auth.invalid_credentials", 401),
  CREDENTIAL_ID_INVALID("AUTH_002", "error.auth.credential_id_invalid", 400),
  PASSWORD_INVALID("AUTH_003", "error.auth.password_invalid", 400),
  TOKEN_GENERATION_FAILED("AUTH_004", "error.auth.token_generation_failed", 500),
  UNCATEGORIZED_EXCEPTION("AUTH_005", "error.auth.uncategorized", 500),
  PHONE_ALREADY_REGISTERED("AUTH_006", "error.auth.phone_already_registered", 400),
  INVALID_VERIFICATION_CODE_REQUEST(
      "AUTH_007", "error.auth.invalid_verification_code_request", 400),
  INVALID_VERIFICATION_CODE("AUTH_008", "error.auth.invalid_verification_code", 400),
  INVALID_REGISTER_COMMAND("AUTH_009", "error.auth.invalid_register_command", 400),
  EMAIL_ALREADY_VERIFIED("AUTH_010", "error.auth.email_already_verified", 400),
  USER_UNVERIFIED("AUTH_011", "error.auth.user_unverified", 403),
  USER_BLOCKED("AUTH_012", "error.auth.user_blocked", 403),
  ROLE_NOT_FOUND("AUTH_013", "error.auth.role_not_found", 500),
  EMAIL_ALREADY_USED("AUTH_014", "error.auth.email_already_used", 400),
  OAUTH2_AUTHORIZATION_REQUEST_FAILED(
      "AUTH_015", "error.auth.oauth2_authorization_request_failed", 401),
  INVALID_TOKEN("AUTH_016", "error.auth.invalid_token", 401),
  ACCESS_DENIED("AUTH_017", "error.auth.access_denied", 403),
  TOO_MANY_REQUESTS("AUTH_018", "error.auth.too_many_requests", 429),
  ROLE_CANNOT_BE_DELETED("AUTH_019", "error.auth.role_cannot_be_deleted", 400),
  TOKEN_EXPIRED("AUTH_020", "error.auth.token_expired", 401),
  AUTHENTICATION_REQUIRED("AUTH_021", "error.auth.authentication_required", 401),
  SYSTEM_ROLE_MODIFICATION("AUTH_022", "error.auth.system_role_modification", 400),
  PERMISSION_NOT_FOUND("AUTH_023", "error.auth.permission_not_found", 400),
  MFA_METHOD_ALREADY_EXISTS("AUTH_024", "error.auth.mfa_method_already_exists", 400),
  MFA_METHOD_NOT_FOUND("AUTH_025", "error.auth.mfa_method_not_found", 404),
  MFA_USER_NOT_VERIFIED("AUTH_026", "error.auth.mfa_user_not_verified", 403),
  CANNOT_ASSIGN_ADMIN_ROLE("AUTH_027", "error.auth.cannot_assign_admin_role", 400);
  private final String code;
  private final String messageKey;
  private final int httpStatus;

  AuthErrorCode(String code, String messageKey, int httpStatus) {
    this.code = code;
    this.messageKey = messageKey;
    this.httpStatus = httpStatus;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String getMessageKey() {
    return messageKey;
  }

  @Override
  public int getHttpStatus() {
    return httpStatus;
  }
}
