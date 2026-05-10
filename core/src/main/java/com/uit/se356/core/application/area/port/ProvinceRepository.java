package com.uit.se356.core.application.area.port;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.core.application.area.projections.ProvinceSummaryProjection;
import com.uit.se356.core.domain.entities.area.Province;
import com.uit.se356.core.domain.vo.area.ProvinceId;
import java.util.List;
import java.util.Optional;

public interface ProvinceRepository {
  Province create(Province province);

  List<Province> createAll(List<Province> provinces);

  Province update(Province province);

  Optional<Province> findById(ProvinceId id);

  PageResponse<ProvinceSummaryProjection> findAll(SearchPageable pageable);

  boolean existsByCode(String code);

  void deleteById(ProvinceId id);

  Optional<Province> findByCode(String provinceCode);
}
