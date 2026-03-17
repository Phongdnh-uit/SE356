package com.uit.se356.core.domain.vo.depot;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.core.domain.exception.DepotErrorCode;

public record DepotId(String value) {
  public DepotId {
    if (value == null || value.isBlank()) {
      throw new AppException(DepotErrorCode.INVALID_DEPOT_ID);
    }
  }
}
