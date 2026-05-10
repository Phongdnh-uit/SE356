package com.uit.se356.core.presentation.rest.order;

import com.uit.se356.common.dto.ApiResponse;
import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.common.services.CommandBus;
import com.uit.se356.common.services.QueryBus;
import com.uit.se356.core.application.order.command.AssignDriverCommand;
import com.uit.se356.core.application.order.command.CancelOrderCommand;
import com.uit.se356.core.application.order.command.ConfirmOrderCommand;
import com.uit.se356.core.application.order.command.CreateOrderCommand;
import com.uit.se356.core.application.order.command.DeliverOrderCommand;
import com.uit.se356.core.application.order.command.RejectOrderCommand;
import com.uit.se356.core.application.order.command.StartDeliveryCommand;
import com.uit.se356.core.application.order.command.UpdateRecipientCommand;
import com.uit.se356.core.application.order.projections.OrderDetailProjection;
import com.uit.se356.core.application.order.projections.OrderSummaryProjection;
import com.uit.se356.core.application.order.query.OrderDetailQuery;
import com.uit.se356.core.application.order.query.OrderSummaryQuery;
import com.uit.se356.core.application.order.result.OrderResult;
import com.uit.se356.core.presentation.dto.order.AssignDriverRequest;
import com.uit.se356.core.presentation.dto.order.DeliverOrderRequest;
import com.uit.se356.core.presentation.dto.order.RejectOrderRequest;
import com.uit.se356.core.presentation.dto.order.UpdateRecipientRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;

@Tag(name = "Order Management")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final CommandBus commandBus;
  private final QueryBus queryBus;

  // ==================== CREATE OPERATIONS ====================
  @Operation(summary = "Create a new Order")
  @PostMapping
  //  @PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
  public ResponseEntity<ApiResponse<OrderResult>> createOrder(
      @RequestBody CreateOrderCommand command) {
    OrderResult result = commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.created(result, "Order created successfully"));
  }

  // ==================== UPDATE OPERATIONS ====================
  @Operation(summary = "Confirm Order")
  @PatchMapping("/{orderId}/confirm")
  //  @PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
  public ResponseEntity<ApiResponse<OrderResult>> confirmOrder(@PathVariable String orderId) {
    ConfirmOrderCommand command = new ConfirmOrderCommand(orderId);
    OrderResult result = commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.ok(result, "Order confirmed successfully"));
  }

  @Operation(summary = "Assign Driver to Order")
  @PatchMapping("/{orderId}/assign")
  //  @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
  public ResponseEntity<ApiResponse<OrderResult>> assignDriver(
      @PathVariable String orderId, @RequestBody AssignDriverRequest request) {
    AssignDriverCommand command =
        new AssignDriverCommand(orderId, request.driverId(), request.depotId());
    OrderResult result = commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.ok(result, "Driver assigned successfully"));
  }

  @Operation(summary = "Start Order Delivery")
  @PatchMapping("/{orderId}/start-delivery")
  //  @PreAuthorize("hasAnyRole('DRIVER', 'DISPATCHER', 'ADMIN')")
  public ResponseEntity<ApiResponse<OrderResult>> startDelivery(@PathVariable String orderId) {
    StartDeliveryCommand command = new StartDeliveryCommand(orderId);
    OrderResult result = commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.ok(result, "Delivery started successfully"));
  }

  @Operation(summary = "Deliver Order")
  @PatchMapping("/{orderId}/deliver")
  //  @PreAuthorize("hasAnyRole('DRIVER', 'DISPATCHER', 'ADMIN')")
  public ResponseEntity<ApiResponse<OrderResult>> deliverOrder(
      @PathVariable String orderId, @RequestBody DeliverOrderRequest request) {
    DeliverOrderCommand command = new DeliverOrderCommand(orderId, request.deliveryDate());
    OrderResult result = commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.ok(result, "Order delivered successfully"));
  }

  @Operation(summary = "Reject Order")
  @PatchMapping("/{orderId}/reject")
  //  @PreAuthorize("hasAnyRole('DISPATCHER', 'ADMIN')")
  public ResponseEntity<ApiResponse<OrderResult>> rejectOrder(
      @PathVariable String orderId, @RequestBody RejectOrderRequest request) {
    RejectOrderCommand command = new RejectOrderCommand(orderId, request.rejectionReason());
    OrderResult result = commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.ok(result, "Order rejected successfully"));
  }

  @Operation(summary = "Cancel Order")
  @PatchMapping("/{orderId}/cancel")
  //  @PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
  public ResponseEntity<ApiResponse<OrderResult>> cancelOrder(@PathVariable String orderId) {
    CancelOrderCommand command = new CancelOrderCommand(orderId);
    OrderResult result = commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.ok(result, "Order cancelled successfully"));
  }

  @Operation(summary = "Update Recipient Information")
  @PatchMapping("/{orderId}/recipient")
  //  @PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN')")
  public ResponseEntity<ApiResponse<OrderResult>> updateRecipient(
      @PathVariable String orderId, @RequestBody UpdateRecipientRequest request) {
    UpdateRecipientCommand command =
        new UpdateRecipientCommand(
            orderId, request.recipientName(), request.recipientPhone(), request.recipientAddress());
    OrderResult result = commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.ok(result, "Recipient information updated successfully"));
  }

  // ==================== READ OPERATIONS ====================
  @Operation(summary = "Get Order by ID")
  @GetMapping("/{orderId}")
  //  @PreAuthorize("hasAnyRole('MERCHANT', 'DRIVER', 'DISPATCHER', 'ADMIN')")
  public ResponseEntity<ApiResponse<OrderDetailProjection>> getOrderById(
      @PathVariable String orderId) {
    OrderDetailQuery query = new OrderDetailQuery(orderId);
    OrderDetailProjection result = queryBus.dispatch(query);
    return ResponseEntity.ok(ApiResponse.ok(result, "Order retrieved successfully"));
  }

  @Operation(
      summary = "Get list of Orders with pagination and filtering",
      description =
          "Hỗ trợ phân trang, sắp xếp và filter RSQL. Ví dụ: status==PENDING;type==EXPRESS")
  @GetMapping
  //  @PreAuthorize("hasAnyRole('MERCHANT', 'DRIVER', 'DISPATCHER', 'ADMIN')")
  public ResponseEntity<ApiResponse<PageResponse<OrderSummaryProjection>>> getAllOrders(
      @ParameterObject @ModelAttribute SearchPageable pageable) {
    OrderSummaryQuery query = new OrderSummaryQuery(pageable);
    PageResponse<OrderSummaryProjection> result = queryBus.dispatch(query);
    return ResponseEntity.ok(ApiResponse.ok(result, "Orders retrieved successfully"));
  }

  @Operation(summary = "Get Order by Tracking Code")
  @GetMapping("/tracking/{trackingCode}")
  //  @PreAuthorize("hasAnyRole('MERCHANT', 'DRIVER', 'DISPATCHER', 'ADMIN')")
  public ResponseEntity<ApiResponse<OrderDetailProjection>> getOrderByTrackingCode(
      @PathVariable String trackingCode) {
    OrderDetailProjection result = queryBus.dispatch(new OrderDetailQuery(trackingCode));

    return ResponseEntity.ok(
        ApiResponse.ok(result, "Order retrieved successfully by tracking code"));
  }

  // ==================== DELETE OPERATIONS ====================
  @Operation(summary = "Delete Order")
  @DeleteMapping("/{orderId}")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable String orderId) {

    return ResponseEntity.ok(ApiResponse.ok(null, "Order deleted successfully"));
  }
}
