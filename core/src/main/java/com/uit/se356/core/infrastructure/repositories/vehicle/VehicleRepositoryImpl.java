package com.uit.se356.core.infrastructure.repositories.vehicle;

import com.uit.se356.core.application.vehicle.port.VehicleRepository;
import com.uit.se356.core.domain.entities.vehicle.Vehicle;
import com.uit.se356.core.infrastructure.persistence.entities.vehicle.VehicleJpaEntity;
import com.uit.se356.core.infrastructure.persistence.mappers.vehicle.VehiclePersistenceMapper;
import com.uit.se356.core.infrastructure.persistence.repositories.vehicle.VehicleJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VehicleRepositoryImpl implements VehicleRepository {
  private final VehicleJpaRepository vehicleJpaRepository;
  private final VehiclePersistenceMapper vehiclePersistenceMapper;

  @Override
  public Vehicle save(Vehicle vehicle) {
    VehicleJpaEntity entity = vehiclePersistenceMapper.toEntity(vehicle);
    VehicleJpaEntity savedEntity = vehicleJpaRepository.save(entity);
    return vehiclePersistenceMapper.toDomain(savedEntity);
  }

  @Override
  public Optional<Vehicle> findById(String id) {
    return vehicleJpaRepository.findById(id).map(vehiclePersistenceMapper::toDomain);
  }

  @Override
  public void delete(String id) {
    vehicleJpaRepository.deleteById(id);
  }

  @Override
  public boolean existsByLicensePlate(String licensePlate) {
    return vehicleJpaRepository.existsByLicensePlate(licensePlate);
  }

  @Override
  public boolean existsByLicensePlateAndIdNot(String licensePlate, String id) {
    return vehicleJpaRepository.existsByLicensePlateAndIdNot(licensePlate, id);
  }

  @Override
  public boolean existsByShipperId(String shipperId) {
    return vehicleJpaRepository.existsByShipperId(shipperId);
  }

  @Override
  public boolean existsByShipperIdAndIdNot(String shipperId, String id) {
    return vehicleJpaRepository.existsByShipperIdAndIdNot(shipperId, id);
  }
}
