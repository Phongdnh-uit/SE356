package com.uit.se356.delivery.infrastructure.optimization.solver;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;

public class VrpConstraintProvider implements ConstraintProvider {

  @Override
  public Constraint[] defineConstraints(ConstraintFactory factory) {
    return new Constraint[]{
        vehicleCapacityWeightConstraint(factory),
        minimizeTotalDistanceConstraint(factory)
    };
  }

  // 1. HARD CONSTRAINT: Không được nhét quá tải trọng xe
  private Constraint vehicleCapacityWeightConstraint(ConstraintFactory factory) {
    return factory.forEach(VehicleRoutingEntity.class)
        .filter(vehicle -> {
          // Tính tổng khối lượng của các task gán cho xe này
          double totalWeight = vehicle.getTasks().stream()
              .mapToDouble(TaskRoutingEntity::getWeight)
              .sum();
          return totalWeight > vehicle.getMaxWeight();
        })
        // Nếu vượt quá, phạt điểm HARD tương ứng với phần dư
        .penalize(HardSoftScore.ONE_HARD,
            vehicle -> (int) (calculateTotalWeight(vehicle) - vehicle.getMaxWeight()))
        .asConstraint("Over capacity (Weight)");
  }

  // 2. SOFT CONSTRAINT: Quãng đường càng ngắn càng tốt
  private Constraint minimizeTotalDistanceConstraint(ConstraintFactory factory) {
    return factory.forEach(VehicleRoutingEntity.class)
        .penalize(HardSoftScore.ONE_SOFT, vehicle -> {
          // Logic tính tổng quãng đường dựa trên tọa độ các task trong vehicle.getTasks()
          // (Trong thực tế, bạn sẽ gọi GraphHopper Matrix ở đây để lấy khoảng cách thực)
          return calculateRouteDistance(vehicle);
        })
        .asConstraint("Minimize distance");
  }

  private double calculateTotalWeight(VehicleRoutingEntity vehicle) {
    return vehicle.getTasks().stream().mapToDouble(TaskRoutingEntity::getWeight).sum();
  }

  private int calculateRouteDistance(VehicleRoutingEntity vehicle) {
    // Dummy logic - Bạn sẽ thay bằng tính toán từ Distance Matrix
    return vehicle.getTasks().size() * 10;
  }
}