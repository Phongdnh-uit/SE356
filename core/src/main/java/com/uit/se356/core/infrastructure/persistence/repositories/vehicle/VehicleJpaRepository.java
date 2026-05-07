package com.uit.se356.core.infrastructure.persistence.repositories.vehicle;

import com.uit.se356.common.repository.CommonRepository;
import com.uit.se356.core.infrastructure.persistence.entities.vehicle.VehicleJpaEntity;

public interface VehicleJpaRepository extends CommonRepository<VehicleJpaEntity, String> {
  boolean existsByLicensePlate(String licensePlate);

  boolean existsByLicensePlateAndIdNot(String licensePlate, String id);

  boolean existsByShipperId(String shipperId);

  boolean existsByShipperIdAndIdNot(String shipperId, String id);
}
