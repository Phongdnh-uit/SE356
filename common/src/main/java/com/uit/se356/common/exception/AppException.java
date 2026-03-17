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
}
