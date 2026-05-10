package com.uit.se356.core.application.area.port;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.core.application.area.projections.WardSummaryProjection;
import com.uit.se356.core.domain.entities.area.Ward;
import com.uit.se356.core.domain.vo.area.WardId;
import java.util.List;
import java.util.Optional;

public interface WardRepository {
  Ward create(Ward ward);

  List<Ward> createAll(List<Ward> wards);

  Ward update(Ward ward);

  Optional<Ward> findById(WardId id);

  PageResponse<WardSummaryProjection> findAll(SearchPageable pageable);

  boolean existsByCode(String code);

  void deleteById(WardId id);
}
