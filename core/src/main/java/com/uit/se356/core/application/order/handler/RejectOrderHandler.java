package com.uit.se356.core.application.order.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.order.command.RejectOrderCommand;
import com.uit.se356.core.application.order.port.OrderRepository;
import com.uit.se356.core.application.order.result.OrderResult;
import com.uit.se356.core.domain.entities.order.Order;
import com.uit.se356.core.domain.exception.OrderErrorCode;
import com.uit.se356.core.domain.vo.order.OrderId;

public class RejectOrderHandler implements CommandHandler<RejectOrderCommand, OrderResult> {

  private final OrderRepository orderRepository;

  public RejectOrderHandler(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Override
  public OrderResult handle(RejectOrderCommand command) {
    Order order =
        orderRepository
            .findById(new OrderId(command.orderId()))
            .orElseThrow(() -> new AppException(OrderErrorCode.ORDER_NOT_FOUND));

    order.rejectOrder(command.rejectionReason());
    Order updatedOrder = orderRepository.update(order);
    return OrderResult.fromEntity(updatedOrder);
  }
}
