package com.uit.se356.core.domain.vo.ticket;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;

public record TicketId(String value) {
  public TicketId {
    if (value == null || value.isBlank()) {
      throw new AppException(CommonErrorCode.INVALID_ID_FORMAT);
    }
  }
}
