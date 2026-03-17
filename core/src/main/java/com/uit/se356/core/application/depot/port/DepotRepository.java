package com.uit.se356.core.application.depot.port;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.core.application.depot.projection.DepotSummaryProjection;
import com.uit.se356.core.domain.entities.depot.Depot;
import com.uit.se356.core.domain.vo.depot.DepotId;
import java.util.Optional;

public interface DepotRepository {
  Depot create(Depot depot);

  Depot update(Depot depot);

  void delete(DepotId id);

  Optional<Depot> findById(DepotId id);

  PageResponse<DepotSummaryProjection> findAll(SearchPageable searchCriteria);

  boolean hasNearbyDepot(double lat, double lng, double radiusInKm, DepotId excludeDepotId);
}
