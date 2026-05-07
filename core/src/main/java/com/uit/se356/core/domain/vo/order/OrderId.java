package com.uit.se356.core.domain.vo.order;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.core.domain.exception.OrderErrorCode;

public record OrderId(String value) {
  public OrderId {
    if (value == null || value.isBlank()) {
      throw new AppException(OrderErrorCode.INVALID_ORDER_ID);
    }
  }
}
