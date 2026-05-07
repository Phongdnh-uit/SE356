package com.uit.se356.core.infrastructure.middleware;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MetricTracker {

  private final MeterRegistry registry;

  /**
   * Đo đạc toàn diện một hành động (action) bất kỳ.
   *
   * @param component Tên thành phần (ví dụ: "command", "query", "api", "repository")
   * @param operation Tên thao tác cụ thể (ví dụ: "CreateOrder", "GetUser", "PaypalClient")
   * @param action Logic cần thực thi
   */
  public <R> R observe(String component, String operation, Supplier<R> action) {
    Timer timer =
        Timer.builder("system.execution.latency")
            .description("Time taken to execute a specific operation in the system")
            .tag("component", component)
            .tag("operation", operation)
            .publishPercentileHistogram()
            .register(registry);

    try {
      R result = timer.record(action);
      incrementStatus(component, operation, "SUCCESS");
      return result;
    } catch (Exception e) {
      incrementStatus(component, operation, "FAILURE");
      incrementError(component, operation, e.getClass().getSimpleName());
      throw e;
    }
  }

  private void incrementStatus(String component, String operation, String status) {
    Counter.builder("system.execution.status")
        .tag("component", component)
        .tag("operation", operation)
        .tag("status", status)
        .register(registry)
        .increment();
  }

  private void incrementError(String component, String operation, String errorType) {
    Counter.builder("system.execution.errors")
        .tag("component", component)
        .tag("operation", operation)
        .tag("error_type", errorType)
        .register(registry)
        .increment();
  }
}
