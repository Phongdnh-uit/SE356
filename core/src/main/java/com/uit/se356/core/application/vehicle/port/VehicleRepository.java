package com.uit.se356.core.application.vehicle.port;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.core.application.vehicle.projecttion.VehicleSummaryProjection;
import com.uit.se356.core.domain.entities.vehicle.Vehicle;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.vehicle.VehicleId;
import java.util.Optional;

public interface VehicleRepository {
  Vehicle save(Vehicle vehicle); // Dùng chung cho Create/Update

  Optional<Vehicle> findById(VehicleId id);

  PageResponse<VehicleSummaryProjection> findAll(SearchPageable pageable);

  void delete(VehicleId id);

  boolean existsByLicensePlate(String licensePlate);

  boolean existsByLicensePlateAndIdNot(String licensePlate, VehicleId id);

  boolean existsByShipperId(UserId shipperId);

  boolean existsByShipperIdAndIdNot(UserId shipperId, VehicleId id);
}
