package com.uit.se356.core.application.vehicle.port;

import com.uit.se356.core.domain.entities.vehicle.Vehicle;
import java.util.Optional;

public interface VehicleRepository {
  Vehicle save(Vehicle vehicle); // Dùng chung cho Create/Update

  Optional<Vehicle> findById(String id);

  void delete(String id);

  boolean existsByLicensePlate(String licensePlate);

  boolean existsByLicensePlateAndIdNot(String licensePlate, String id);

  boolean existsByShipperId(String shipperId);

  boolean existsByShipperIdAndIdNot(String shipperId, String id);
}
