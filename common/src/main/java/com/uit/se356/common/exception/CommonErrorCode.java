package com.uit.se356.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommonErrorCode implements ErrorCode {
  EMAIL_SENDING_FAILED("COMMON-0001", "error.common.email.sending_failed", 500),
  INVALID_ID_FORMAT("COMMON-0000", "error.common.invalid_id_format", 400),
  VALIDATION_ERROR("COMMON-0002", "error.common.validation_error", 400),
  INTERNAL_ERROR("COMMON-0003", "error.common.internal_error", 500),
  INVALID_SORT_ORDER("COMMON-0004", "error.common.invalid_sort_order", 400),
  FIELD_REQUIRED("COMMON-0005", "error.common.field_required", 400),
  RESOURCE_NOT_FOUND("COMMON-0006", "error.common.resource_not_found", 404),
  UNCATEGORIZED_EXCEPTION("COMMON-0007", "error.common.uncategorized_exception", 500),
  ALREADY_EXISTS("COMMON-0008", "error.common.already_exists", 409),
  FIELD_INVALID("COMMON-0009", "error.common.field_invalid", 400);
  private final String code;
  private final String messageKey;
  private final int httpStatus;
}
