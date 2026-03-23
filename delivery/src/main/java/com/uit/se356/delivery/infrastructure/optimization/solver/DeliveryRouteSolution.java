package com.uit.se356.delivery.infrastructure.optimization.solver;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.HardSoftScore;

import java.util.List;

@PlanningSolution
public class DeliveryRouteSolution {

  // Danh sách các điểm cần giao (Dữ liệu cố định, không thay đổi)
  @ValueRangeProvider(id = "taskRange")
  @ProblemFactCollectionProperty
  private List<TaskRoutingEntity> taskList;

  // Danh sách các xe (Chứa biến List<Task> sẽ bị thay đổi bởi thuật toán)
  @PlanningEntityCollectionProperty
  private List<VehicleRoutingEntity> vehicleList;

  // Điểm số của phương án
  @PlanningScore
  private HardSoftScore score;

  public DeliveryRouteSolution() {}

  public DeliveryRouteSolution(List<TaskRoutingEntity> taskList, List<VehicleRoutingEntity> vehicleList) {
    this.taskList = taskList;
    this.vehicleList = vehicleList;
  }

  // Getters and Setters
  public List<TaskRoutingEntity> getTaskList() {
    return taskList;
  }
  public void setTaskList(List<TaskRoutingEntity> taskList) {
    this.taskList = taskList;
  }
  public List<VehicleRoutingEntity> getVehicleList() {
    return vehicleList;
  }
  public void setVehicleList(List<VehicleRoutingEntity> vehicleList) {
    this.vehicleList = vehicleList;
  }
  public HardSoftScore getScore() {
    return score;
  }
  public void setScore(HardSoftScore score) {
    this.score = score;
  }
}