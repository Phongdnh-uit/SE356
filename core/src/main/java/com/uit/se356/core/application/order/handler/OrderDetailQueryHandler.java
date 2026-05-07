package com.uit.se356.core.application.order.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.order.port.OrderRepository;
import com.uit.se356.core.application.order.projections.OrderDetailProjection;
import com.uit.se356.core.application.order.query.OrderDetailQuery;
import com.uit.se356.core.domain.exception.OrderErrorCode;
import com.uit.se356.core.domain.vo.order.OrderId;

public class OrderDetailQueryHandler
    implements QueryHandler<OrderDetailQuery, OrderDetailProjection> {

  private final OrderRepository orderRepository;

  public OrderDetailQueryHandler(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Override
  public OrderDetailProjection handle(OrderDetailQuery query) {
    return orderRepository
        .findDetailById(new OrderId(query.orderId()))
        .orElseThrow(() -> new AppException(OrderErrorCode.ORDER_NOT_FOUND));
  }
}
