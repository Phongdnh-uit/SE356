package com.uit.se356.core.application.authentication.handler.permission;

import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.authentication.port.out.PermissionRepository;
import com.uit.se356.core.application.authentication.projections.PermissionSummaryProjection;
import com.uit.se356.core.application.authentication.query.role.GetPermissionsByRoleQuery;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetPermissionByRoleHandler
    implements QueryHandler<
        GetPermissionsByRoleQuery, Map<String, List<PermissionSummaryProjection>>> {

  private final PermissionRepository permissionRepository;

  public GetPermissionByRoleHandler(PermissionRepository permissionRepository) {
    this.permissionRepository = permissionRepository;
  }

  @Override
  public Map<String, List<PermissionSummaryProjection>> handle(GetPermissionsByRoleQuery query) {
    var list = permissionRepository.findAllByRoleId(query.roleId(), query.pageable());
    return list.stream()
        .collect(Collectors.groupingByConcurrent(PermissionSummaryProjection::getResource));
  }
}
