package com.uit.se356.core.domain.exception;

import com.uit.se356.common.exception.ErrorCode;

public enum DepotErrorCode implements ErrorCode {
  DEPOT_NOT_FOUND("DEPOT_001", "error.depot.not_found", 404),
  DEPOT_IN_USE("DEPOT_002", "error.depot.in_use", 400), // Lỗi khi xóa kho đang có tuyến xe
  INVALID_COORDINATE("DEPOT_003", "error.depot.invalid_coordinate", 400),
  INVALID_DEPOT_ID("DEPOT_004", "error.depot.invalid_id", 400),
  DEPOT_TOO_CLOSE("DEPOT_004", "error.depot.too_close", 400);

  private final String code;
  private final String messageKey;
  private final int httpStatus;

  DepotErrorCode(String code, String messageKey, int httpStatus) {
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
