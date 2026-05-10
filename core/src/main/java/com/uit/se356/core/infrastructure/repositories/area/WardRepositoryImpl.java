package com.uit.se356.core.infrastructure.repositories.area;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.common.utils.PageableUtil;
import com.uit.se356.core.application.area.port.WardRepository;
import com.uit.se356.core.application.area.projections.WardSummaryProjection;
import com.uit.se356.core.application.area.projections.WardSummaryProjectionImpl;
import com.uit.se356.core.domain.entities.area.Ward;
import com.uit.se356.core.domain.vo.area.WardId;
import com.uit.se356.core.infrastructure.persistence.entities.area.WardJpaEntity;
import com.uit.se356.core.infrastructure.persistence.mappers.area.WardPersistenceMapper;
import com.uit.se356.core.infrastructure.persistence.repositories.area.WardJpaRepository;
import io.github.perplexhub.rsql.RSQLJPASupport;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class WardRepositoryImpl implements WardRepository {

  private final WardJpaRepository wardJpaRepository;
  private final WardPersistenceMapper wardPersistenceMapper;

  @Override
  @Transactional
  public Ward create(Ward ward) {
    WardJpaEntity wardJpaEntity = wardPersistenceMapper.toEntity(ward);
    WardJpaEntity savedEntity = wardJpaRepository.save(wardJpaEntity);
    return wardPersistenceMapper.toDomain(savedEntity);
  }

  @Override
  @Transactional
  public List<Ward> createAll(List<Ward> wards) {
    List<WardJpaEntity> entities = wards.stream().map(wardPersistenceMapper::toEntity).toList();
    return wardJpaRepository.saveAll(entities).stream()
        .map(wardPersistenceMapper::toDomain)
        .toList();
  }

  @Override
  @Transactional
  public Ward update(Ward ward) {
    Optional<WardJpaEntity> existingEntityOpt = wardJpaRepository.findById(ward.getId().value());
    if (existingEntityOpt.isEmpty()) {
      throw new RuntimeException("Ward not found with id: " + ward.getId());
    }

    WardJpaEntity existingEntity = existingEntityOpt.get();
    wardPersistenceMapper.updateEntityFromDomain(ward, existingEntity);
    WardJpaEntity updatedEntity = wardJpaRepository.save(existingEntity);
    return wardPersistenceMapper.toDomain(updatedEntity);
  }

  @Override
  public Optional<Ward> findById(WardId id) {
    return wardJpaRepository.findById(id.value()).map(wardPersistenceMapper::toDomain);
  }

  @Override
  public PageResponse<WardSummaryProjection> findAll(SearchPageable searchCriteria) {
    Specification<WardJpaEntity> spec = RSQLJPASupport.toSpecification(searchCriteria.filter());
    Pageable pageable = PageableUtil.createPageable(searchCriteria);
    var page = wardJpaRepository.findAll(spec, pageable);
    return PageResponse.from(
        page,
        wardEntity ->
            new WardSummaryProjectionImpl(
                wardEntity.getId(),
                wardEntity.getCode(),
                wardEntity.getName(),
                wardEntity.getType(),
                wardEntity.getProvinceId(),
                wardEntity.getProvince() != null ? wardEntity.getProvince().getName() : null));
  }

  @Override
  public boolean existsByCode(String code) {
    return wardJpaRepository.existsByCode(code);
  }

  @Override
  public void deleteById(WardId id) {
    wardJpaRepository.deleteById(id.value());
  }
}
