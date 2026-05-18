package com.uit.se356.core.presentation.rest.payment;

import com.uit.se356.common.dto.ApiResponse;
import com.uit.se356.common.services.CommandBus;
import com.uit.se356.common.utils.SecurityUtil;
import com.uit.se356.core.application.order.command.PayOrderCommand;
import com.uit.se356.core.domain.vo.authentication.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment Management")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
  private final CommandBus commandBus;
  private final SecurityUtil<UserId> securityUtil;

  @Operation(summary = "Pay for an Order using Wallet")
  @PostMapping("/orders/{orderId}/pay")
  public ResponseEntity<ApiResponse<Void>> payOrder(@PathVariable String orderId) {
    UserId userId = securityUtil.getCurrentUserPrincipal().get().getId();
    PayOrderCommand command = new PayOrderCommand(orderId, userId);
    commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.ok(null, "Order paid successfully"));
  }
}
