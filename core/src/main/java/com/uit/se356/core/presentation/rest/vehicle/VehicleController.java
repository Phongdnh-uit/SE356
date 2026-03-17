package com.uit.se356.core.presentation.rest.vehicle;

import com.uit.se356.common.dto.ApiResponse;
import com.uit.se356.common.services.CommandBus;
import com.uit.se356.core.application.vehicle.command.SaveVehicleCommand;
import com.uit.se356.core.application.vehicle.result.VehicleResult;
import com.uit.se356.core.presentation.dto.vehicle.VehicleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Vehicle Management")
@RestController
@RequestMapping("/api/v1/admin/vehicles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class VehicleController {

  private final CommandBus commandBus;

  @Operation(summary = "Create a new Vehicle")
  @PostMapping
  public ResponseEntity<ApiResponse<VehicleResult>> createVehicle(
      @RequestBody VehicleRequest request) {
    SaveVehicleCommand command =
        new SaveVehicleCommand(
            null,
            request.licensePlate(),
            request.type(),
            request.maxWeight(),
            request.maxVolume(),
            request.shipperId());
    VehicleResult result = commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.created(result, "Vehicle created successfully"));
  }

  @Operation(summary = "Update an existing Vehicle")
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<VehicleResult>> updateVehicle(
      @PathVariable String id, @RequestBody VehicleRequest request) {

    SaveVehicleCommand command =
        new SaveVehicleCommand(
            id,
            request.licensePlate(),
            request.type(),
            request.maxWeight(),
            request.maxVolume(),
            request.shipperId());
    VehicleResult result = commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.ok(result, "Vehicle updated successfully"));
  }
}
