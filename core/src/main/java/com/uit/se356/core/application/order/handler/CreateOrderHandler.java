package com.uit.se356.core.application.order.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.IdGenerator;
import com.uit.se356.core.application.order.command.CreateOrderCommand;
import com.uit.se356.core.application.order.port.OrderRepository;
import com.uit.se356.core.application.order.result.OrderResult;
import com.uit.se356.core.domain.entities.order.Order;
import com.uit.se356.core.domain.exception.OrderErrorCode;
import com.uit.se356.core.domain.vo.area.ProvinceId;
import com.uit.se356.core.domain.vo.area.WardId;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.order.Dimensions;
import com.uit.se356.core.domain.vo.order.OrderId;

public class CreateOrderHandler implements CommandHandler<CreateOrderCommand, OrderResult> {

  private final OrderRepository orderRepository;
  private final IdGenerator idGenerator;

  public CreateOrderHandler(OrderRepository orderRepository, IdGenerator idGenerator) {
    this.orderRepository = orderRepository;
    this.idGenerator = idGenerator;
  }

  @Override
  public OrderResult handle(CreateOrderCommand command) {
    if (orderRepository.existsByTrackingCode(command.trackingCode())) {
      throw new AppException(OrderErrorCode.TRACKING_CODE_ALREADY_EXISTS);
    }

    String newId = idGenerator.generate().toString();
    Dimensions dimensions =
        new Dimensions(command.dimLength(), command.dimWidth(), command.dimHeight());

    Order order =
        Order.createNewOrder(
            new OrderId(newId),
            command.trackingCode(),
            command.type(),
            new UserId(command.customerId()),
            new UserId(command.senderId()),
            command.senderName(),
            command.senderPhone(),
            command.senderAddress(),
            new WardId(command.senderWardId()),
            new ProvinceId(command.senderProvinceId()),
            command.recipientName(),
            command.recipientPhone(),
            command.recipientAddress(),
            new WardId(command.recipientWardId()),
            new ProvinceId(command.recipientProvinceId()),
            command.description(),
            command.weight(),
            dimensions,
            command.valueDeclared(),
            command.fragile(),
            command.requiresSignature(),
            command.shippingFee(),
            command.insuranceFee());

    Order savedOrder = orderRepository.save(order);
    return OrderResult.fromEntity(savedOrder);
  }
}
