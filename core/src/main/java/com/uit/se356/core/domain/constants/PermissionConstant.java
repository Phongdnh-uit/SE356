package com.uit.se356.core.domain.constants;

public interface PermissionConstant {
  public interface Resource {
    String INTERNAL = "internal";
  }

  public interface Action {
    String READ_SUMMARY = "read_summary";
    String READ = "read";
    String CREATE = "create";
    String UPDATE = "update";
    String DELETE = "delete";
    String ASSIGN = "assign";
  }
}
