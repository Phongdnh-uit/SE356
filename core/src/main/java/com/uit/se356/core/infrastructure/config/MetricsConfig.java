package com.uit.se356.core.infrastructure.config;

import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
  @Bean
  MeterFilter disableActiveMetricsFilter() {
    // Chặn toàn bộ các metric có tên chứa "active" để nhẹ máy
    return MeterFilter.deny(
        id -> {
          String name = id.getName();
          return name.startsWith("http.server.requests.active")
              || name.startsWith("http.client.requests.active");
        });
  }
}
