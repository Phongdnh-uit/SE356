package com.uit.se356.core.application.authentication.port.out;

import com.uit.se356.common.dto.SearchRequest;
import com.uit.se356.core.application.authentication.projections.PermissionSummaryProjection;
import com.uit.se356.core.domain.entities.authentication.Permission;
import com.uit.se356.core.domain.vo.authentication.PermissionId;
import com.uit.se356.core.domain.vo.authentication.RoleId;
import java.util.List;
import java.util.Set;

public interface PermissionRepository {
  Permission create(Permission newPermission);

  Permission update(Permission permissionToUpdate);

  boolean existsByCode(String code);

  void deleteAllById(Set<PermissionId> permissionIds);

  List<PermissionSummaryProjection> findAllByRoleId(RoleId roleId);

  List<PermissionSummaryProjection> findAll(SearchRequest pageable);

  List<Permission> findAll();

  Set<PermissionId> findExistingIds(Set<PermissionId> permissionIds);

  List<PermissionSummaryProjection> findAllByRoleId(RoleId roleId, SearchRequest searchRequest);
}
