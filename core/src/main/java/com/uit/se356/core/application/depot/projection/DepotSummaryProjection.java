package com.uit.se356.core.application.depot.projection;

import com.uit.se356.core.domain.vo.depot.DepotType;

public interface DepotSummaryProjection {
  String getId();

  String getName();

  DepotType getType();

  Double getLat();

  Double getLng();

  boolean isStartNode();
}
