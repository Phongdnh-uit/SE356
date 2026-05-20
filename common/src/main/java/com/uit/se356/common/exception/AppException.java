package com.uit.se356.common.exception;

import java.io.Serial;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

  @Serial static final long serialVersionUID = 1L;

  private final ErrorCode errorCode;
  private final Object details;

  public AppException(ErrorCode errorCode) {
    super(errorCode.getMessageKey());
    this.errorCode = errorCode;
    this.details = null;
  }

  public AppException(ErrorCode errorCode, Object details) {
    super(errorCode.getMessageKey());
    this.errorCode = errorCode;
    this.details = details;
  }

  // 1. Constructor for just an error code and a root cause
  public AppException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessageKey(), cause);
    this.errorCode = errorCode;
    this.details = null;
  }

  // 2. Constructor for an error code, custom details, and a root cause
  public AppException(ErrorCode errorCode, Object details, Throwable cause) {
    super(errorCode.getMessageKey(), cause);
    this.errorCode = errorCode;
    this.details = details;
  }
}
