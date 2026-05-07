package com.uit.se356.core.domain.exception;

import com.uit.se356.common.exception.ErrorCode;

public enum OrderErrorCode implements ErrorCode {
  INVALID_ORDER_ID("ORDER_001", "order.error.invalid_id", 400),
  INVALID_TRACKING_CODE("ORDER_002", "order.error.invalid_tracking_code", 400),
  INVALID_WEIGHT("ORDER_003", "order.error.invalid_weight", 400),
  INVALID_SHIPPING_FEE("ORDER_004", "order.error.invalid_shipping_fee", 400),
  INVALID_ORDER_STATUS("ORDER_005", "order.error.invalid_status", 400),
  INVALID_REJECTION_REASON("ORDER_006", "order.error.invalid_rejection_reason", 400),
  CANNOT_CANCEL_ORDER("ORDER_007", "order.error.cannot_cancel", 409),
  CANNOT_UPDATE_RECIPIENT("ORDER_008", "order.error.cannot_update_recipient", 409),
  ORDER_NOT_FOUND("ORDER_009", "order.error.not_found", 404),
  TRACKING_CODE_ALREADY_EXISTS("ORDER_010", "order.error.tracking_code_exists", 409);

  private final String code;
  private final String messageKey;
  private final int httpStatus;

  OrderErrorCode(String code, String messageKey, int httpStatus) {
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
