package com.uit.se356.core.application.order.handler;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.order.port.OrderRepository;
import com.uit.se356.core.application.order.projections.OrderSummaryProjection;
import com.uit.se356.core.application.order.query.OrderSummaryQuery;

public class OrderSummaryQueryHandler
    implements QueryHandler<OrderSummaryQuery, PageResponse<OrderSummaryProjection>> {

  private final OrderRepository orderRepository;

  public OrderSummaryQueryHandler(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Override
  public PageResponse<OrderSummaryProjection> handle(OrderSummaryQuery query) {
    return orderRepository.findAll(query.pageable());
  }
}
