package com.uit.se356.core.infrastructure.repositories.depot;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.common.utils.PageableUtil;
import com.uit.se356.core.application.depot.port.DepotRepository;
import com.uit.se356.core.application.depot.projection.DepotSummaryProjection;
import com.uit.se356.core.domain.entities.depot.Depot;
import com.uit.se356.core.domain.vo.depot.DepotId;
import com.uit.se356.core.infrastructure.persistence.entities.depot.DepotJpaEntity;
import com.uit.se356.core.infrastructure.persistence.mappers.depot.DepotPersistenceMapper;
import com.uit.se356.core.infrastructure.persistence.repositories.depot.DepotJpaRepository;
import io.github.perplexhub.rsql.RSQLJPASupport;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class DepotRepositoryImpl implements DepotRepository {

  private final DepotJpaRepository repository;
  private final DepotPersistenceMapper mapper;

  @Override
  public Depot create(Depot depot) {
    DepotJpaEntity entity = mapper.toEntity(depot);
    DepotJpaEntity savedEntity = repository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public Depot update(Depot depot) {
    DepotJpaEntity entity =
        repository
            .findById(depot.getId().value())
            .orElseThrow(
                () -> new RuntimeException("Depot not found with id: " + depot.getId().value()));
    mapper.updateEntityFromDomain(depot, entity);
    DepotJpaEntity updatedEntity = repository.save(entity);
    return mapper.toDomain(updatedEntity);
  }

  @Override
  public void delete(DepotId id) {
    repository.deleteById(id.value());
  }

  @Override
  public Optional<Depot> findById(DepotId id) {
    return repository.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  public PageResponse<DepotSummaryProjection> findAll(SearchPageable searchCriteria) {
    // 1. Chuyển đổi filter sang RSQL
    Specification<DepotJpaEntity> spec = RSQLJPASupport.toSpecification(searchCriteria.filter());
    // 2. Tạo pageable với sort
    Pageable pageable = PageableUtil.createPageable(searchCriteria);
    var page = repository.findBy(spec, q -> q.as(DepotSummaryProjection.class).page(pageable));
    return PageResponse.from(page);
  }

  @Override
  public boolean hasNearbyDepot(double lat, double lng, double radiusInKm, DepotId excludeDepotId) {
    return repository.existsDepotWithinRadius(lat, lng, radiusInKm, excludeDepotId.value());
  }
}
