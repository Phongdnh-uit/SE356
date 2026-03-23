package com.uit.se356.delivery.application.services;

import ai.timefold.solver.core.api.solver.SolverManager;
import com.uit.se356.delivery.application.ports.in.OptimizeRouteUseCase;
import com.uit.se356.delivery.application.ports.out.VehicleInfoPort;
import com.uit.se356.delivery.domain.entities.DeliveryTask;
import com.uit.se356.delivery.domain.entities.DeliveryVehicle;
import com.uit.se356.delivery.infrastructure.optimization.solver.DeliveryRouteSolution;
import com.uit.se356.delivery.infrastructure.optimization.solver.TaskRoutingEntity;
import com.uit.se356.delivery.infrastructure.optimization.solver.VehicleRoutingEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteOptimizationService implements OptimizeRouteUseCase {

  // SolverManager do Timefold tự động inject thông qua Spring Boot Starter
  private final SolverManager<DeliveryRouteSolution> solverManager;

  // Các Port để lấy dữ liệu (Ví dụ)
  private final VehicleInfoPort vehicleInfoPort;
  // private final DeliveryTaskRepository taskRepository;

  @Override
  public void optimize(String planId) {
    log.info("Bắt đầu chuẩn bị bài toán cho planId: {}", planId);

    // 1. Hàm solveAndListen của Timefold sẽ chạy BẤT ĐỒNG BỘ trên một ThreadPool riêng của nó.
    // Cấu trúc: solveAndListen(jobId, hàm_load_dữ_liệu, hàm_lưu_kết_quả)
    solverManager.solveAndListen(
        planId,                         // Job ID (có thể là planId hoặc một UUID)
        this::loadProblemData,          // Hàm load dữ liệu và tạo Solution ban đầu
        this::saveFinalSolution         // Hàm được gọi khi có Solution mới (kể cả intermediate)
    );
  }

  /**
   * Hàm này có nhiệm vụ Lấy dữ liệu Domain -> Map sang Solver Entity -> Tạo Solution
   */
  private DeliveryRouteSolution loadProblemData(String planId) {
    // A. Lấy dữ liệu từ Database / Core module (Dùng Mock cho ví dụ)
    List<DeliveryVehicle> domainVehicles = vehicleInfoPort.getAvailableVehicles(planId);
    List<DeliveryTask> domainTasks = /* taskRepository.findByPlanId(planId) */ List.of();

    // B. Ép kiểu (Mapping) sang class của thuật toán
    List<VehicleRoutingEntity> solverVehicles = domainVehicles.stream()
        .map(v -> new VehicleRoutingEntity(
            v.getId().toString(),
            v.getCapacity().maxWeight(),
            v.getCapacity().maxVolume()
        )).collect(Collectors.toList());

    List<TaskRoutingEntity> solverTasks = domainTasks.stream()
        .map(t -> new TaskRoutingEntity(
            t.getId().toString(),
            t.getLocation().latitude(),
            t.getLocation().longitude(),
            t.getDemand().maxWeight(),
            t.getDemand().maxVolume()
        )).collect(Collectors.toList());

    // C. Khởi tạo Bức tranh tổng thể (Solution)
    log.info("Đã load xong dữ liệu: {} xe, {} đơn hàng.", solverVehicles.size(), solverTasks.size());
    return new DeliveryRouteSolution(solverTasks, solverVehicles);
  }

  /**
   * Hàm này được Timefold gọi TỰ ĐỘNG khi thuật toán tìm ra kết quả cuối cùng (hoặc hết thời gian chạy)
   */
  private void saveFinalSolution(DeliveryRouteSolution finalSolution) {
    log.info("Thuật toán đã chạy xong! Điểm số: {}", finalSolution.getScore());

    // A. Duyệt qua từng chiếc xe trong Solution để xem nó được gán các Task nào
    for (VehicleRoutingEntity vehicle : finalSolution.getVehicleList()) {
      log.info("Xe {}: được phân công {} đơn hàng.", vehicle.getVehicleId(), vehicle.getTasks().size());

      // B. (Thực tế) Map ngược lại từ VehicleRoutingEntity sang Route (Domain)
      // Route newRoute = new Route(...);

      // C. Lưu Database
      // routeRepository.save(newRoute);
    }

    // D. Cuối cùng, có thể đẩy một Event qua Kafka hoặc WebSocket để báo cho App Shipper
    // notificationPort.notifyShippers(finalSolution);
  }
}