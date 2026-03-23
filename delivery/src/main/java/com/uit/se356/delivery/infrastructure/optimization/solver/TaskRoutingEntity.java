package com.uit.se356.delivery.infrastructure.optimization.solver;

import java.util.Objects;

/**
 * Đại diện cho một đơn hàng / điểm cần giao trong không gian của thuật toán.
 * Class này chứa các dữ liệu tĩnh không bị thay đổi trong quá trình giải bài toán.
 */
public class TaskRoutingEntity {

  private String taskId;      // Dùng để map ngược lại với OrderId/DeliveryTaskId của tầng Domain
  private double latitude;
  private double longitude;

  // Các thông số về sức chứa để ConstraintProvider tính toán phạt điểm
  private double weight;
  private double volume;

  // Thường nên có No-arg constructor cho các thao tác Reflection hoặc Serialize/Deserialize
  public TaskRoutingEntity() {
  }

  public TaskRoutingEntity(String taskId, double latitude, double longitude, double weight, double volume) {
    this.taskId = taskId;
    this.latitude = latitude;
    this.longitude = longitude;
    this.weight = weight;
    this.volume = volume;
  }

  // -------- Getters và Setters --------
  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public double getVolume() {
    return volume;
  }

  public void setVolume(double volume) {
    this.volume = volume;
  }

  // -------- Các hàm hỗ trợ cho Constraint Provider (Luật tính điểm) --------

  /**
   * Hàm hỗ trợ tính khoảng cách đường chim bay cơ bản (Haversine).
   * Trong môi trường Production, bạn sẽ thay thế hàm này bằng việc tra cứu
   * Distance Matrix (GraphHopper) đã được nạp sẵn trên RAM.
   */
  public double getDistanceTo(TaskRoutingEntity other) {
    if (this.latitude == other.latitude && this.longitude == other.longitude) {
      return 0.0;
    }
    double earthRadius = 6371; // Bán kính trái đất (km)
    double dLat = Math.toRadians(other.latitude - this.latitude);
    double dLon = Math.toRadians(other.longitude - this.longitude);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return earthRadius * c;
  }

  // Override equals và hashCode rất quan trọng để OptaPlanner so sánh các object trong bộ nhớ
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TaskRoutingEntity that = (TaskRoutingEntity) o;
    return Objects.equals(taskId, that.taskId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(taskId);
  }

  @Override
  public String toString() {
    return "Task-" + taskId;
  }
}