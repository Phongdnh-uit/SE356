package com.uit.se356.core.application.order.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.uit.se356.common.services.CommandBus;
import com.uit.se356.core.application.order.command.PayOrderCommand;
import com.uit.se356.core.application.order.port.OrderRepository;
import com.uit.se356.core.application.wallet.command.PayWithWalletCommand;
import com.uit.se356.core.domain.entities.order.Order;
import com.uit.se356.core.domain.vo.area.ProvinceId;
import com.uit.se356.core.domain.vo.area.WardId;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.order.Dimensions;
import com.uit.se356.core.domain.vo.order.OrderId;
import com.uit.se356.core.domain.vo.order.OrderStatus;
import com.uit.se356.core.domain.vo.order.OrderType;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PayOrderHandlerTest {

  @Mock private OrderRepository orderRepository;
  @Mock private CommandBus commandBus;

  private PayOrderHandler payOrderHandler;

  @BeforeEach
  void setUp() {
    payOrderHandler = new PayOrderHandler(orderRepository, commandBus);
  }

  @Test
  void testHandle_Success() {
    // Arrange
    String orderIdStr = "order-123";
    UserId userId = new UserId("user-456");
    PayOrderCommand command = new PayOrderCommand(orderIdStr, userId);

    Order order =
        Order.createNewOrder(
            new OrderId(orderIdStr),
            "TRACK-123",
            OrderType.EXPRESS,
            userId,
            new UserId("sender-1"),
            "Sender Name",
            "0123456789",
            "Sender Address",
            new WardId("ward-1"),
            new ProvinceId("prov-1"),
            "Recipient Name",
            "0987654321",
            "Recipient Address",
            new WardId("ward-2"),
            new ProvinceId("prov-2"),
            "Desc",
            1.0f,
            new Dimensions(10.0, 10.0, 10.0),
            BigDecimal.valueOf(100000),
            false,
            false,
            BigDecimal.valueOf(20000),
            BigDecimal.valueOf(5000));

    when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));

    // Act
    payOrderHandler.handle(command);

    // Assert
    assertEquals(OrderStatus.PAID, order.getStatus());
    verify(commandBus, times(1)).dispatch(any(PayWithWalletCommand.class));
    verify(orderRepository, times(1)).save(order);
  }
}
