package com.uit.se356.core.infrastructure.repositories.area;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.utils.PageableUtil;
import com.uit.se356.core.application.area.port.ProvinceRepository;
import com.uit.se356.core.application.area.projections.ProvinceSummaryProjection;
import com.uit.se356.core.domain.entities.area.Province;
import com.uit.se356.core.domain.exception.AreaErrorCode;
import com.uit.se356.core.domain.vo.area.ProvinceId;
import com.uit.se356.core.infrastructure.persistence.entities.area.ProvinceJpaEntity;
import com.uit.se356.core.infrastructure.persistence.mappers.area.ProvincePersistenceMapper;
import com.uit.se356.core.infrastructure.persistence.repositories.area.ProvinceJpaRepository;
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
public class ProvinceRepositoryImpl implements ProvinceRepository {

  private final ProvinceJpaRepository provinceJpaRepository;
  private final ProvincePersistenceMapper provincePersistenceMapper;

  @Override
  @Transactional
  public Province create(Province province) {
    ProvinceJpaEntity entity = provincePersistenceMapper.toEntity(province);
    ProvinceJpaEntity savedProvince = provinceJpaRepository.save(entity);
    return provincePersistenceMapper.toDomain(savedProvince);
  }

  @Override
  @Transactional
  public List<Province> createAll(List<Province> provinces) {
    List<ProvinceJpaEntity> entities =
        provinces.stream().map(provincePersistenceMapper::toEntity).toList();
    return provinceJpaRepository.saveAll(entities).stream()
        .map(provincePersistenceMapper::toDomain)
        .toList();
  }

  @Override
  @Transactional
  public Province update(Province province) {
    ProvinceJpaEntity existingEntity =
        provinceJpaRepository
            .findById(province.getId().value())
            .orElseThrow(
                () -> new AppException(AreaErrorCode.PROVINCE_NOT_FOUND, province.getId()));
    provincePersistenceMapper.updateEntityFromDomain(province, existingEntity);
    ProvinceJpaEntity updatedEntity = provinceJpaRepository.save(existingEntity);
    return provincePersistenceMapper.toDomain(updatedEntity);
  }

  @Override
  public Optional<Province> findById(ProvinceId id) {
    return provinceJpaRepository.findById(id.value()).map(provincePersistenceMapper::toDomain);
  }

  @Override
  public PageResponse<ProvinceSummaryProjection> findAll(SearchPageable searchCriteria) {
    Specification<ProvinceJpaEntity> spec = RSQLJPASupport.toSpecification(searchCriteria.filter());
    Pageable pageable = PageableUtil.createPageable(searchCriteria);
    var page =
        provinceJpaRepository.findBy(
            spec, q -> q.as(ProvinceSummaryProjection.class).page(pageable));
    return PageResponse.from(page);
  }

  @Override
  public boolean existsByCode(String code) {
    return provinceJpaRepository.existsByCode(code);
  }

  @Override
  public void deleteById(ProvinceId id) {
    provinceJpaRepository.deleteById(id.value());
  }

  @Override
  public Optional<Province> findByCode(String provinceCode) {
    return provinceJpaRepository.findByCode(provinceCode).map(provincePersistenceMapper::toDomain);
  }
}
