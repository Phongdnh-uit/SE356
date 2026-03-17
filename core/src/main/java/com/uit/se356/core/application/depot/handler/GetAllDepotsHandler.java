package com.uit.se356.core.application.depot.handler;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.depot.port.DepotRepository;
import com.uit.se356.core.application.depot.projection.DepotSummaryProjection;
import com.uit.se356.core.application.depot.query.GetAllDepotsQuery;

public class GetAllDepotsHandler
    implements QueryHandler<GetAllDepotsQuery, PageResponse<DepotSummaryProjection>> {

  private final DepotRepository repository;

  public GetAllDepotsHandler(DepotRepository repository) {
    this.repository = repository;
  }

  @Override
  public PageResponse<DepotSummaryProjection> handle(GetAllDepotsQuery query) {
    return repository.findAll(query.pageable());
  }
}
