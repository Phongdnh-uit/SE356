package com.uit.se356.core.application.area.query;

import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.dto.Query;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.area.result.ProvinceResult;
import com.uit.se356.core.domain.vo.area.ProvinceId;
import java.util.ArrayList;
import java.util.List;

public record GetProvinceByIdQuery(ProvinceId id) implements Query<ProvinceResult> {
  public GetProvinceByIdQuery {
    List<FieldError> errors = new ArrayList<>();
    if (id == null) {
      errors.add(
          new FieldError(
              "id", CommonErrorCode.FIELD_REQUIRED.getMessageKey(), new Object[] {"id"}));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
