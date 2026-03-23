package com.uit.se356.delivery.domain.entities;

import java.util.List;

public class Route {
  private final DeliveryVehicle vehicle;
  private final List<DeliveryTask> tasks; // Thứ tự các điểm cần giao
  private final double totalDistance;
  private final double totalTime;

  private Route(
      DeliveryVehicle vehicle, List<DeliveryTask> tasks, double totalDistance, double totalTime) {
    this.vehicle = vehicle;
    this.tasks = tasks;
    this.totalDistance = totalDistance;
    this.totalTime = totalTime;
  }

  // -------- Factory Method --------
  public static Route create(
      DeliveryVehicle vehicle, List<DeliveryTask> tasks, double totalDistance, double totalTime) {
    return new Route(vehicle, tasks, totalDistance, totalTime);
  }

  // ---------------- Getters ----------------
  public DeliveryVehicle getVehicle() {
    return vehicle;
  }

  public List<DeliveryTask> getTasks() {
    return tasks;
  }

  public double getTotalDistance() {
    return totalDistance;
  }

  public double getTotalTime() {
    return totalTime;
  }
}
