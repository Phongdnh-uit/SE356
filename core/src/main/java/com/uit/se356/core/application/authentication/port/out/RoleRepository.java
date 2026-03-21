package com.uit.se356.core.application.authentication.port.out;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.core.application.authentication.projections.RoleSummaryProjection;
import com.uit.se356.core.domain.entities.authentication.Role;
import com.uit.se356.core.domain.vo.authentication.RoleId;
import java.util.Optional;

public interface RoleRepository {

  Role create(Role newRole);

  Role update(Role roleToUpdate);

  Optional<Role> findById(RoleId roleId);

  Optional<Role> findDefault();

  Optional<Role> findByName(String name);

  boolean existsByName(String name);

  boolean existsById(RoleId roleId);

  void delete(Role role);

  PageResponse<RoleSummaryProjection> findAll(SearchPageable searchCriteria);
}
