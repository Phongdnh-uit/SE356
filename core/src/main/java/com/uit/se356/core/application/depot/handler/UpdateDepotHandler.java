package com.uit.se356.core.application.depot.handler;

import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.depot.command.UpdateDepotCommand;
import com.uit.se356.core.application.depot.port.DepotRepository;
import com.uit.se356.core.application.depot.result.DepotResult;
import com.uit.se356.core.domain.constants.PermissionConstant;
import com.uit.se356.core.domain.entities.depot.Depot;
import com.uit.se356.core.domain.exception.DepotErrorCode;
import com.uit.se356.core.domain.vo.area.Coordinate;
import java.util.ArrayList;
import java.util.List;

public class UpdateDepotHandler implements CommandHandler<UpdateDepotCommand, DepotResult> {
  private final DepotRepository depotRepository;

  // Định nghĩa khoảng cách tối thiểu (7 km)
  private static final double MIN_DISTANCE_KM = 7.0;

  public UpdateDepotHandler(DepotRepository depotRepository) {
    this.depotRepository = depotRepository;
  }

  @HasPermission(
      name = "Update Depot",
      description = "Permission to update a depot",
      resource = PermissionConstant.Resource.DEPOT,
      action = PermissionConstant.Action.UPDATE)
  @Override
  public DepotResult handle(UpdateDepotCommand command) {
    List<FieldError> errors = new ArrayList<>();
    // BR: Kiểm tra khoảng cách tối thiểu với các kho hiện tại
    if (depotRepository.hasNearbyDepot(
        command.lat(), command.lng(), MIN_DISTANCE_KM, command.id())) {
      errors.add(
          new FieldError(
              String.valueOf(MIN_DISTANCE_KM),
              "error.depot.too_close",
              new Object[] {MIN_DISTANCE_KM}));
    }
    if (!errors.isEmpty()) {
      throw new AppException(DepotErrorCode.DEPOT_TOO_CLOSE, MIN_DISTANCE_KM);
    }
    Depot depot =
        depotRepository
            .findById(command.id())
            .orElseThrow(() -> new AppException(DepotErrorCode.DEPOT_NOT_FOUND));

    Coordinate coordinate = new Coordinate(command.lat(), command.lng());
    depot.update(command.name(), command.type(), coordinate);

    Depot updatedDepot = depotRepository.update(depot);
    return DepotResult.fromEntity(updatedDepot);
  }
}
