package com.uit.se356.core.infrastructure.persistence.mappers.vehicle;

import com.uit.se356.core.domain.entities.vehicle.Vehicle;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.vehicle.PhysicalCapacity;
import com.uit.se356.core.domain.vo.vehicle.VehicleId;
import com.uit.se356.core.infrastructure.persistence.entities.vehicle.VehicleJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehiclePersistenceMapper {
  public Vehicle toDomain(VehicleJpaEntity entity) {
    if (entity == null) return null;

    return Vehicle.rehydrate(
        new VehicleId(entity.getId()),
        entity.getLicensePlate(),
        entity.getType(),
        new PhysicalCapacity(entity.getMaxWeight(), entity.getMaxVolume()),
        new UserId(entity.getShipperId()));
  }

  public VehicleJpaEntity toEntity(Vehicle domain) {
    if (domain == null) return null;

    VehicleJpaEntity entity = new VehicleJpaEntity();
    entity.setId(domain.getId().value());
    entity.setLicensePlate(domain.getLicensePlate());
    entity.setType(domain.getType());
    entity.setMaxWeight(domain.getCapacity().maxWeight());
    entity.setMaxVolume(domain.getCapacity().maxVolume());
    entity.setShipperId(domain.getShipperId().value());
    return entity;
  }

  public void updateEntityFromDomain(Vehicle domain, VehicleJpaEntity entity) {
    if (domain == null || entity == null) return;

    entity.setLicensePlate(domain.getLicensePlate());
    entity.setType(domain.getType());
    entity.setMaxWeight(domain.getCapacity().maxWeight());
    entity.setMaxVolume(domain.getCapacity().maxVolume());
    entity.setShipperId(domain.getShipperId().value());
  }
}
