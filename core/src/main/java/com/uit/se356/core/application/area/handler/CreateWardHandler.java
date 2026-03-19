package com.uit.se356.core.application.area.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.IdGenerator;
import com.uit.se356.core.application.area.command.CreateWardCommand;
import com.uit.se356.core.application.area.port.ProvinceRepository;
import com.uit.se356.core.application.area.port.WardRepository;
import com.uit.se356.core.application.area.result.WardResult;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.entities.area.Ward;
import com.uit.se356.core.domain.exception.AreaErrorCode;
import com.uit.se356.core.domain.vo.area.WardId;

public class CreateWardHandler implements CommandHandler<CreateWardCommand, WardResult> {

  private final WardRepository wardRepository;
  private final ProvinceRepository provinceRepository;
  private final IdGenerator idGenerator;

  public CreateWardHandler(
      WardRepository wardRepository,
      ProvinceRepository provinceRepository,
      IdGenerator idGenerator) {
    this.wardRepository = wardRepository;
    this.provinceRepository = provinceRepository;
    this.idGenerator = idGenerator;
  }

  @HasPermission(
      name = "Create Ward",
      description = "Permission to create a new ward",
      resource = PermissionConstant.Resource.WARD,
      action = PermissionConstant.Action.CREATE)
  @Override
  public WardResult handle(CreateWardCommand command) {
    if (wardRepository.existsByCode(command.code())) {
      throw new AppException(AreaErrorCode.DUPLICATE_WARD_CODE);
    }

    provinceRepository
        .findById(command.provinceId())
        .orElseThrow(
            () ->
                new AppException(
                    AreaErrorCode.PROVINCE_NOT_FOUND, "The specified Province ID does not exist."));

    Ward newWard =
        Ward.createNewWard(
            new WardId(idGenerator.generate().toString()),
            command.code(),
            command.name(),
            command.provinceId(),
            command.type(),
            command.polygon());
    Ward savedWard = wardRepository.create(newWard);

    return WardResult.fromEntity(savedWard);
  }
}
