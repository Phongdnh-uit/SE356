package com.uit.se356.core.domain.entities.authentication;

import com.uit.se356.core.domain.vo.authentication.PermissionId;
import java.util.Objects;

public class Permission {
  private final PermissionId id;
  private String name;
  private String description;
  private String resource;
  private String action;
  private String condition;

  private Permission(
      PermissionId id,
      String name,
      String description,
      String resource,
      String action,
      String condition) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.resource = resource;
    this.action = action;
    this.condition = condition;
  }

  // ============================ FACTORY ============================
  public static Permission create(
      PermissionId id,
      String name,
      String description,
      String resource,
      String action,
      String condition) {
    Objects.requireNonNull(id);
    Objects.requireNonNull(name);
    Objects.requireNonNull(resource);
    Objects.requireNonNull(action);
    return new Permission(id, name, description, resource, action, condition);
  }

  public static Permission rehydrate(
      PermissionId id,
      String name,
      String description,
      String resource,
      String action,
      String condition) {
    Objects.requireNonNull(id);
    Objects.requireNonNull(name);
    Objects.requireNonNull(resource);
    Objects.requireNonNull(action);
    return new Permission(id, name, description, resource, action, condition);
  }

  // ============================ BEHAVIORS ============================
  public void update(String name, String description, String condition) {
    Objects.requireNonNull(name);
    this.name = name;
    this.description = description;
    this.condition = condition;
  }

  // ============================ GETTERS ============================
  public PermissionId getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getResource() {
    return resource;
  }

  public String getAction() {
    return action;
  }

  public String getCondition() {
    return condition;
  }
}
