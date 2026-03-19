package com.uit.se356.core.application.authentication.projections;

public interface PermissionSummaryProjection {
  String getId();

  String getName();

  String getDescription();

  String getResource();

  String getAction();
}
