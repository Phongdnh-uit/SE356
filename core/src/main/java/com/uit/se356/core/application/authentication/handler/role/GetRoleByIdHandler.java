package com.uit.se356.core.application.authentication.handler.role;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.authentication.port.out.RoleRepository;
import com.uit.se356.core.application.authentication.query.role.GetRoleByIdQuery;
import com.uit.se356.core.application.authentication.result.RoleResult;

public class GetRoleByIdHandler implements QueryHandler<GetRoleByIdQuery, RoleResult> {
  private final RoleRepository roleRepository;

  public GetRoleByIdHandler(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @Override
  public RoleResult handle(GetRoleByIdQuery query) {
    var role = roleRepository.findById(query.id());
    if (role.isEmpty()) {
      throw new AppException(CommonErrorCode.RESOURCE_NOT_FOUND);
    }
    return RoleResult.from(role.get());
  }
}
