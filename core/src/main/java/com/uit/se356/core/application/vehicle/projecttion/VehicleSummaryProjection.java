package com.uit.se356.core.application.vehicle.projecttion;

public interface VehicleSummaryProjection {
  String getId();

  String getLicensePlate();

  String getType();

  Double getMaxWeight();

  Double getMaxVolume();

  String getShipperId();
}
