package com.uit.se356.delivery.infrastructure.optimization.solver;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;

import java.util.ArrayList;
import java.util.List;

@PlanningEntity
public class VehicleRoutingEntity {

  private String vehicleId;
  private double maxWeight;
  private double maxVolume;

  @PlanningListVariable(valueRangeProviderRefs = "taskRange")
  private List<TaskRoutingEntity> tasks = new ArrayList<>();

  public VehicleRoutingEntity() {
    // OptaPlanner/Timefold bắt buộc phải có No-arg constructor
  }

  public VehicleRoutingEntity(String vehicleId, double maxWeight, double maxVolume) {
    this.vehicleId = vehicleId;
    this.maxWeight = maxWeight;
    this.maxVolume = maxVolume;
  }

  // Getters and Setters
  public String getVehicleId() {
    return vehicleId;
  }
  public void setVehicleId(String vehicleId) {
    this.vehicleId = vehicleId;
  }
  public double getMaxWeight() {
    return maxWeight;
  }
  public void setMaxWeight(double maxWeight) {
    this.maxWeight = maxWeight;
  }
  public double getMaxVolume() {
    return maxVolume;
  }
  public void setMaxVolume(double maxVolume) {
    this.maxVolume = maxVolume;
  }
  public List<TaskRoutingEntity> getTasks() {
    return tasks;
  }
  public void setTasks(List<TaskRoutingEntity> tasks) {
    this.tasks = tasks;
  }
}
