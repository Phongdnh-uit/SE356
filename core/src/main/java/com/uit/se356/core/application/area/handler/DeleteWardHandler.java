package com.uit.se356.core.application.area.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.area.command.DeleteWardCommand;
import com.uit.se356.core.application.area.port.WardRepository;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.exception.AreaErrorCode;

public class DeleteWardHandler implements CommandHandler<DeleteWardCommand, Void> {
  private final WardRepository wardRepository;

  public DeleteWardHandler(WardRepository wardRepository) {
    this.wardRepository = wardRepository;
  }

  @HasPermission(
      name = "Delete Ward",
      description = "Permission to delete a ward",
      resource = PermissionConstant.Resource.WARD,
      action = PermissionConstant.Action.DELETE)
  @Override
  public Void handle(DeleteWardCommand command) {
    wardRepository
        .findById(command.id())
        .orElseThrow(() -> new AppException(AreaErrorCode.WARD_NOT_FOUND, command.id()));
    wardRepository.deleteById(command.id());
    return null;
  }
}
