package com.uit.se356.core.application.order.port;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.core.application.order.projections.OrderDetailProjection;
import com.uit.se356.core.application.order.projections.OrderSummaryProjection;
import com.uit.se356.core.domain.entities.order.Order;
import com.uit.se356.core.domain.vo.order.OrderId;
import java.util.Optional;

public interface OrderRepository {
  Order save(Order order);

  Order update(Order order);

  Optional<Order> findById(OrderId id);

  Optional<Order> findByTrackingCode(String trackingCode);

  Optional<OrderDetailProjection> findDetailById(OrderId id);

  PageResponse<OrderSummaryProjection> findAll(SearchPageable pageable);

  boolean existsByTrackingCode(String trackingCode);

  void deleteById(OrderId id);
}
