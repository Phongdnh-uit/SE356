package com.uit.se356.core.application.area.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.area.command.UpdateProvinceCommand;
import com.uit.se356.core.application.area.port.ProvinceRepository;
import com.uit.se356.core.application.area.result.ProvinceResult;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.entities.area.Province;
import com.uit.se356.core.domain.exception.AreaErrorCode;
import java.util.Optional;

public class UpdateProvinceHandler
    implements CommandHandler<UpdateProvinceCommand, ProvinceResult> {
  private final ProvinceRepository provinceRepository;

  public UpdateProvinceHandler(ProvinceRepository provinceRepository) {
    this.provinceRepository = provinceRepository;
  }

  @HasPermission(
      name = "Update Province",
      description = "Permission to update a province",
      resource = PermissionConstant.Resource.PROVINCE,
      action = PermissionConstant.Action.UPDATE)
  @Override
  public ProvinceResult handle(UpdateProvinceCommand command) {
    Optional<Province> provinceOpt = provinceRepository.findById(command.id());
    if (provinceOpt.isEmpty()) {
      throw new AppException(AreaErrorCode.PROVINCE_NOT_FOUND);
    }

    if (provinceRepository.existsByCode(command.code())) {
      throw new AppException(AreaErrorCode.DUPLICATE_PROVINCE_CODE);
    }

    Province existingProvince = provinceOpt.get();
    existingProvince.update(command.code(), command.name(), command.type());
    Province updatedProvince = provinceRepository.update(existingProvince);

    return ProvinceResult.fromEntity(updatedProvince);
  }
}
