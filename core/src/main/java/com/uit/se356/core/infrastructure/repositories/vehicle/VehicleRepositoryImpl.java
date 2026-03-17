package com.uit.se356.core.infrastructure.repositories.vehicle;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.common.utils.PageableUtil;
import com.uit.se356.core.application.vehicle.port.VehicleRepository;
import com.uit.se356.core.application.vehicle.projecttion.VehicleSummaryProjection;
import com.uit.se356.core.domain.entities.vehicle.Vehicle;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.vehicle.VehicleId;
import com.uit.se356.core.infrastructure.persistence.entities.vehicle.VehicleJpaEntity;
import com.uit.se356.core.infrastructure.persistence.mappers.vehicle.VehiclePersistenceMapper;
import com.uit.se356.core.infrastructure.persistence.repositories.vehicle.VehicleJpaRepository;
import io.github.perplexhub.rsql.RSQLJPASupport;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
  public Optional<Vehicle> findById(VehicleId id) {
    return vehicleJpaRepository.findById(id.value()).map(vehiclePersistenceMapper::toDomain);
  }

  @Override
  public PageResponse<VehicleSummaryProjection> findAll(SearchPageable searchCriteria) {
    Specification<VehicleJpaEntity> spec = RSQLJPASupport.toSpecification(searchCriteria.filter());
    Pageable pageable = PageableUtil.createPageable(searchCriteria);
    var page =
        vehicleJpaRepository.findBy(spec, q -> q.as(VehicleSummaryProjection.class).page(pageable));
    return PageResponse.from(page);
  }

  @Override
  public void delete(VehicleId id) {
    vehicleJpaRepository.deleteById(id.value());
  }

  @Override
  public boolean existsByLicensePlate(String licensePlate) {
    return vehicleJpaRepository.existsByLicensePlate(licensePlate);
  }

  @Override
  public boolean existsByLicensePlateAndIdNot(String licensePlate, VehicleId id) {
    return vehicleJpaRepository.existsByLicensePlateAndIdNot(licensePlate, id.value());
  }

  @Override
  public boolean existsByShipperId(UserId shipperId) {
    return vehicleJpaRepository.existsByShipperId(shipperId.value());
  }

  @Override
  public boolean existsByShipperIdAndIdNot(UserId shipperId, VehicleId id) {
    return vehicleJpaRepository.existsByShipperIdAndIdNot(shipperId.value(), id.value());
  }
}
