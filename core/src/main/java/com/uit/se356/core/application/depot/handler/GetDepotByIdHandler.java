package com.uit.se356.core.application.depot.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.depot.port.DepotRepository;
import com.uit.se356.core.application.depot.query.GetDepotByIdQuery;
import com.uit.se356.core.application.depot.result.DepotResult;
import com.uit.se356.core.domain.entities.depot.Depot;
import com.uit.se356.core.domain.exception.DepotErrorCode;

public class GetDepotByIdHandler implements QueryHandler<GetDepotByIdQuery, DepotResult> {

  private final DepotRepository depotRepository;

  public GetDepotByIdHandler(DepotRepository depotRepository) {
    this.depotRepository = depotRepository;
  }

  @Override
  public DepotResult handle(GetDepotByIdQuery query) {
    Depot depot =
        depotRepository
            .findById(query.id())
            .orElseThrow(() -> new AppException(DepotErrorCode.DEPOT_NOT_FOUND));

    return DepotResult.fromEntity(depot);
  }
}
