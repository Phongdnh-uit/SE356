package com.uit.se356.core.domain.entities.depot;

import com.uit.se356.core.domain.vo.area.Coordinate;
import com.uit.se356.core.domain.vo.depot.DepotId;
import com.uit.se356.core.domain.vo.depot.DepotType;
import java.util.Objects;

public class Depot {
  private final DepotId id;
  private String name;
  private DepotType type;
  private Coordinate coordinate;
  private boolean isStartNode;

  private Depot(
      DepotId id, String name, DepotType type, Coordinate coordinate, boolean isStartNode) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.coordinate = coordinate;
    this.isStartNode = isStartNode;
  }

  // Factory method to create a new Depot
  public static Depot create(DepotId id, String name, DepotType type, Coordinate coordinate) {
    Objects.requireNonNull(id);
    Objects.requireNonNull(name);
    Objects.requireNonNull(type);
    Objects.requireNonNull(coordinate);

    return new Depot(id, name, type, coordinate, false);
  }

  public void update(String name, DepotType type, Coordinate coordinate) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(type);
    Objects.requireNonNull(coordinate);

    this.name = name;
    this.type = type;
    this.coordinate = coordinate;
  }

  public static Depot rehydrate(
      DepotId id, String name, DepotType type, Coordinate coordinate, boolean isStartNode) {
    return new Depot(id, name, type, coordinate, isStartNode);
  }

  // --------------------------- Getters ---------------------------
  public DepotId getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public DepotType getType() {
    return type;
  }

  public Coordinate getCoordinate() {
    return coordinate;
  }

  public boolean isStartNode() {
    return isStartNode;
  }
}
