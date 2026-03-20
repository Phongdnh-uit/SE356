package com.uit.se356.core.application.authentication.query.role;

import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.dto.Query;
import com.uit.se356.common.dto.SearchRequest;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.authentication.projections.PermissionSummaryProjection;
import com.uit.se356.core.domain.vo.authentication.RoleId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record GetPermissionsByRoleQuery(RoleId roleId, SearchRequest pageable)
    implements Query<Map<String, List<PermissionSummaryProjection>>> {
  public GetPermissionsByRoleQuery {
    List<FieldError> errors = new ArrayList<>();
    if (roleId == null) {
      errors.add(new FieldError("roleId", "validation.field.required", new Object[] {"roleId"}));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR);
    }
  }
}
