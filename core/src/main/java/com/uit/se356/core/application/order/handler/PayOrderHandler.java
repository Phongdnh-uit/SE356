package com.uit.se356.core.application.order.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.CommandBus;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.order.command.PayOrderCommand;
import com.uit.se356.core.application.order.port.OrderRepository;
import com.uit.se356.core.application.wallet.command.PayWithWalletCommand;
import com.uit.se356.core.domain.entities.order.Order;
import com.uit.se356.core.domain.exception.OrderErrorCode;
import com.uit.se356.core.domain.vo.order.OrderId;
import org.springframework.transaction.annotation.Transactional;

public class PayOrderHandler implements CommandHandler<PayOrderCommand, Void> {
  private final OrderRepository orderRepository;
  private final CommandBus commandBus;

  public PayOrderHandler(OrderRepository orderRepository, CommandBus commandBus) {
    this.orderRepository = orderRepository;
    this.commandBus = commandBus;
  }

  @Override
  @Transactional
  public Void handle(PayOrderCommand command) {
    Order order =
        orderRepository
            .findById(new OrderId(command.orderId()))
            .orElseThrow(() -> new AppException(OrderErrorCode.ORDER_NOT_FOUND));

    // Thực hiện trừ tiền ví qua CommandBus (gọi sang Wallet Context)
    commandBus.dispatch(
        new PayWithWalletCommand(
            command.userId(),
            order.getTotalAmount(),
            order.getId().toString(),
            "Payment for order: " + order.getTrackingCode()));

    // Cập nhật trạng thái đơn hàng
    order.markAsPaid();
    orderRepository.save(order);

    return null;
  }
}
