package com.uit.se356.core.application.area.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.IdGenerator;
import com.uit.se356.core.application.area.command.CreateProvinceCommand;
import com.uit.se356.core.application.area.port.ProvinceRepository;
import com.uit.se356.core.application.area.result.ProvinceResult;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.entities.area.Province;
import com.uit.se356.core.domain.exception.AreaErrorCode;
import com.uit.se356.core.domain.vo.area.ProvinceId;

public class CreateProvinceHandler
    implements CommandHandler<CreateProvinceCommand, ProvinceResult> {
  private final ProvinceRepository provinceRepository;
  private final IdGenerator idGenerator;

  public CreateProvinceHandler(ProvinceRepository provinceRepository, IdGenerator idGenerator) {
    this.provinceRepository = provinceRepository;
    this.idGenerator = idGenerator;
  }

  @HasPermission(
      name = "Create Province",
      description = "Permission to create a new province",
      resource = PermissionConstant.Resource.PROVINCE,
      action = PermissionConstant.Action.CREATE)
  @Override
  public ProvinceResult handle(CreateProvinceCommand command) {
    if (provinceRepository.existsByCode(command.code())) {
      throw new AppException(AreaErrorCode.DUPLICATE_PROVINCE_CODE);
    }

    Province newProvince =
        Province.create(
            new ProvinceId(idGenerator.generate().toString()),
            command.code(),
            command.name(),
            command.type());

    Province savedProvince = provinceRepository.create(newProvince);
    return ProvinceResult.fromEntity(savedProvince);
  }
}
