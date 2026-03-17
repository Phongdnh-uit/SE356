package com.uit.se356.core.application.depot.port;

import com.uit.se356.core.domain.vo.depot.DepotId;

public interface RouteCheckingPort {
  boolean hasActiveRoutesFromDepot(DepotId id);
}
