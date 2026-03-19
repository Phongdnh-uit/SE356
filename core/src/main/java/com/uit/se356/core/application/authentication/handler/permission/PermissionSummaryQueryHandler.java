package com.uit.se356.core.application.authentication.handler.permission;

import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.authentication.port.out.PermissionRepository;
import com.uit.se356.core.application.authentication.projections.PermissionSummaryProjection;
import com.uit.se356.core.application.authentication.query.permission.PermissionSummaryQuery;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PermissionSummaryQueryHandler
    implements QueryHandler<
        PermissionSummaryQuery, Map<String, List<PermissionSummaryProjection>>> {

  private final PermissionRepository permissionRepository;

  public PermissionSummaryQueryHandler(PermissionRepository permissionRepository) {
    this.permissionRepository = permissionRepository;
  }

  @Override
  public Map<String, List<PermissionSummaryProjection>> handle(PermissionSummaryQuery query) {
    return permissionRepository.findAll(query.pageable()).stream()
        .collect(Collectors.groupingBy(PermissionSummaryProjection::getResource));
  }
}
