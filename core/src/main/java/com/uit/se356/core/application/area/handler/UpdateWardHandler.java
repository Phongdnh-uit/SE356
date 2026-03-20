package com.uit.se356.core.application.area.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.area.command.UpdateWardCommand;
import com.uit.se356.core.application.area.port.ProvinceRepository;
import com.uit.se356.core.application.area.port.WardRepository;
import com.uit.se356.core.application.area.result.WardResult;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.entities.area.Ward;
import com.uit.se356.core.domain.exception.AreaErrorCode;
import java.util.Optional;

public class UpdateWardHandler implements CommandHandler<UpdateWardCommand, WardResult> {
  private final WardRepository wardRepository;
  private final ProvinceRepository provinceRepository;

  public UpdateWardHandler(WardRepository wardRepository, ProvinceRepository provinceRepository) {
    this.wardRepository = wardRepository;
    this.provinceRepository = provinceRepository;
  }

  @HasPermission(
      name = "Update Ward",
      description = "Permission to update a ward",
      resource = PermissionConstant.Resource.WARD,
      action = PermissionConstant.Action.UPDATE)
  @Override
  public WardResult handle(UpdateWardCommand command) {
    Optional<Ward> wardOpt = wardRepository.findById(command.id());
    if (wardOpt.isEmpty()) {
      throw new AppException(AreaErrorCode.WARD_NOT_FOUND);
    }

    if (wardRepository.existsByCode(command.code())) {
      throw new AppException(AreaErrorCode.DUPLICATE_WARD_CODE);
    }

    provinceRepository
        .findById(command.provinceId())
        .orElseThrow(
            () ->
                new AppException(
                    AreaErrorCode.PROVINCE_NOT_FOUND, "The specified Province ID does not exist."));

    Ward existingWard = wardOpt.get();
    existingWard.update(
        command.code(), command.name(), command.provinceId(), command.type(), command.polygon());
    Ward updatedWard = wardRepository.update(existingWard);

    return WardResult.fromEntity(updatedWard);
  }
}
