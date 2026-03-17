package com.uit.se356.core.application.depot.result;

import com.uit.se356.core.domain.entities.depot.Depot;
import com.uit.se356.core.domain.vo.depot.DepotId;
import com.uit.se356.core.domain.vo.depot.DepotType;

public record DepotResult(
    DepotId id, String name, DepotType type, double lat, double lng, boolean isStartNode) {
  public static DepotResult fromEntity(Depot depot) {
    return new DepotResult(
        depot.getId(),
        depot.getName(),
        depot.getType(),
        depot.getCoordinate().latitude(),
        depot.getCoordinate().longitude(),
        depot.isStartNode());
  }
}
