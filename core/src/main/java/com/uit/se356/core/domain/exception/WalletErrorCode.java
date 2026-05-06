package com.uit.se356.core.domain.exception;

import com.uit.se356.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum WalletErrorCode implements ErrorCode {
  WALLET_NOT_FOUND("W001", "error.wallet.not_found", HttpStatus.NOT_FOUND),
  INSUFFICIENT_BALANCE("W002", "error.wallet.insufficient_balance", HttpStatus.BAD_REQUEST),
  INVALID_AMOUNT("W003", "error.wallet.invalid_amount", HttpStatus.BAD_REQUEST),
  TRANSACTION_NOT_FOUND("W004", "error.transaction.not_found", HttpStatus.NOT_FOUND),
  INVALID_TRANSACTION_STATE("W005", "error.transaction.invalid_state", HttpStatus.BAD_REQUEST),
  WALLET_ALREADY_EXISTS("W006", "error.wallet.already_exists", HttpStatus.BAD_REQUEST),
  DEPOSIT_LIMIT_EXCEEDED("W007", "error.wallet.deposit_limit_exceeded", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String messageKey;
  private final HttpStatus httpStatus;

  WalletErrorCode(String code, String messageKey, HttpStatus httpStatus) {
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
    return httpStatus.value();
  }
}
