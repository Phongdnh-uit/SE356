package com.uit.se356.core.application.wallet.query;

import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.dto.Query;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.wallet.result.WalletResult;
import com.uit.se356.core.domain.vo.authentication.UserId;
import java.util.ArrayList;
import java.util.List;

public record GetMyWalletQuery(UserId userId) implements Query<WalletResult> {
  public GetMyWalletQuery {
    List<FieldError> errors = new ArrayList<>();
    if (userId == null) {
      errors.add(
          new FieldError(
              "userId", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"userId"}));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
