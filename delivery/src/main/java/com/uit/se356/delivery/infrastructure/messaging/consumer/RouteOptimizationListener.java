package com.uit.se356.delivery.presentation.messaging.consumers;

import com.uit.se356.delivery.application.ports.in.OptimizeRouteUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RouteOptimizationListener {

  // Gọi vào cổng Inbound của tầng Application (Clean Architecture)
  private final OptimizeRouteUseCase optimizeRouteUseCase;

  @KafkaListener(id = "route-optimizer", topics = "optimize-route-topic", groupId = "delivery-group")
  public void handleOptimizationRequest(Map<String, Object> message, Acknowledgment acknowledgment) {
    try {
      log.info("Nhận yêu cầu tối ưu lộ trình từ Kafka: {}", message);

      // Giả sử message chứa một jobId hoặc planId
      String planId = (String) message.get("planId");

      // Kích hoạt Use Case
      optimizeRouteUseCase.optimize(planId);

      // Xác nhận đã xử lý xong message
      acknowledgment.acknowledge();
    } catch (Exception e) {
      log.error("Lỗi khi xử lý message tối ưu: ", e);
      // Có thể đưa vào Dead Letter Queue (DLQ) ở đây
    }
  }
}