package com.uit.se356.core.application.vehicle.handler;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.vehicle.port.VehicleRepository;
import com.uit.se356.core.application.vehicle.projecttion.VehicleSummaryProjection;
import com.uit.se356.core.application.vehicle.query.GetAllVehiclesQuery;

public class GetAllVehiclesHandler
    implements QueryHandler<GetAllVehiclesQuery, PageResponse<VehicleSummaryProjection>> {

  private final VehicleRepository vehicleRepository;

  public GetAllVehiclesHandler(VehicleRepository vehicleRepository) {
    this.vehicleRepository = vehicleRepository;
  }

  @Override
  public PageResponse<VehicleSummaryProjection> handle(GetAllVehiclesQuery query) {
    return vehicleRepository.findAll(query.pageable());
  }
}
