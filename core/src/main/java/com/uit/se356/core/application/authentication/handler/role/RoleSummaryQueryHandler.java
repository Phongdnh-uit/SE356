package com.uit.se356.core.application.authentication.handler.role;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.authentication.port.out.RoleRepository;
import com.uit.se356.core.application.authentication.projections.RoleSummaryProjection;
import com.uit.se356.core.application.authentication.query.role.RoleSummaryQuery;
import com.uit.se356.core.domain.constants.PermissionConstant;

public class RoleSummaryQueryHandler
    implements QueryHandler<RoleSummaryQuery, PageResponse<RoleSummaryProjection>> {

  private final RoleRepository roleRepository;

  public RoleSummaryQueryHandler(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @HasPermission(
      name = "Read Role Summary",
      description = "Permission to read role summary list",
      resource = PermissionConstant.Resource.ROLE,
      action = PermissionConstant.Action.READ_SUMMARY)
  @Override
  public PageResponse<RoleSummaryProjection> handle(RoleSummaryQuery query) {
    return roleRepository.findAll(query.pageable());
  }
}
