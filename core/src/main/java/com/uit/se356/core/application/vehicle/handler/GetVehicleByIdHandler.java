package com.uit.se356.core.application.vehicle.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.vehicle.port.VehicleRepository;
import com.uit.se356.core.application.vehicle.query.GetVehicleByIdQuery;
import com.uit.se356.core.application.vehicle.result.VehicleResult;
import com.uit.se356.core.domain.entities.vehicle.Vehicle;
import com.uit.se356.core.domain.exception.VehicleErrorCode;

public class GetVehicleByIdHandler implements QueryHandler<GetVehicleByIdQuery, VehicleResult> {

  private final VehicleRepository vehicleRepository;

  public GetVehicleByIdHandler(VehicleRepository vehicleRepository) {
    this.vehicleRepository = vehicleRepository;
  }

  @Override
  public VehicleResult handle(GetVehicleByIdQuery query) {
    Vehicle vehicle =
        vehicleRepository
            .findById(query.id())
            .orElseThrow(() -> new AppException(VehicleErrorCode.VEHICLE_NOT_FOUND));

    return VehicleResult.fromEntity(vehicle);
  }
}
