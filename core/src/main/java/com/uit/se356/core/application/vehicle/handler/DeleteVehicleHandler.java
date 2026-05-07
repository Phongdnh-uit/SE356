package com.uit.se356.core.application.vehicle.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.security.HasPermission;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.vehicle.command.DeleteVehicleCommand;
import com.uit.se356.core.application.vehicle.port.VehicleRepository;
import com.uit.se356.core.domain.exception.VehicleErrorCode;

public class DeleteVehicleHandler implements CommandHandler<DeleteVehicleCommand, Void> {
  private final VehicleRepository vehicleRepository;

  public DeleteVehicleHandler(VehicleRepository vehicleRepository) {
    this.vehicleRepository = vehicleRepository;
  }

  @Override
  @HasPermission("vehicle:delete")
  public Void handle(DeleteVehicleCommand command) {
    vehicleRepository
        .findById(command.id())
        .orElseThrow(() -> new AppException(VehicleErrorCode.VEHICLE_NOT_FOUND));

    vehicleRepository.delete(command.id());
    return null;
  }
}
