package com.uit.se356.core.domain.constants;

public interface PermissionConstant {
  public interface Resource {
    String INTERNAL = "internal";
    String PROVINCE = "province";
    String WARD = "ward";
    String DEPOT = "depot";
    String ROLE = "role";
    String PERMISSION = "permission";
    String TICKET = "ticket";
  }

  public interface Action {
    String READ_SUMMARY = "read_summary";
    String READ = "read";
    String CREATE = "create";
    String UPDATE = "update";
    String DELETE = "delete";
    String ASSIGN = "assign";
    String SYNC = "sync";
  }
}
