package com.uit.se356.core.infrastructure.repositories.depot;

import com.uit.se356.core.application.depot.port.RouteCheckingPort;
import com.uit.se356.core.domain.vo.depot.DepotId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RouteCheckingPortImpl implements RouteCheckingPort {

  @Override
  public boolean hasActiveRoutesFromDepot(DepotId id) {
    return false;
  }
}
