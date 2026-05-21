package com.uit.se356.core.application.order.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.order.port.OrderRepository;
import com.uit.se356.core.application.order.projections.OrderDetailProjection;
import com.uit.se356.core.application.order.query.OrderByTrackingCodeQuery;
import com.uit.se356.core.domain.exception.OrderErrorCode;

public class OrderByTrackingCodeQueryHandler
    implements QueryHandler<OrderByTrackingCodeQuery, OrderDetailProjection> {

  private final OrderRepository orderRepository;

  public OrderByTrackingCodeQueryHandler(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Override
  public OrderDetailProjection handle(OrderByTrackingCodeQuery query) {
    return orderRepository
        .findDetailByTrackingCode(query.trackingCode())
        .orElseThrow(() -> new AppException(OrderErrorCode.ORDER_NOT_FOUND));
  }
}
