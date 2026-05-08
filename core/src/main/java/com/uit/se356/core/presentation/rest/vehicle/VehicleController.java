package com.uit.se356.core.presentation.rest.vehicle;

import com.uit.se356.common.dto.ApiResponse;
import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.common.services.CommandBus;
import com.uit.se356.common.services.QueryBus;
import com.uit.se356.core.application.vehicle.command.DeleteVehicleCommand;
import com.uit.se356.core.application.vehicle.command.SaveVehicleCommand;
import com.uit.se356.core.application.vehicle.projecttion.VehicleSummaryProjection;
import com.uit.se356.core.application.vehicle.query.GetAllVehiclesQuery;
import com.uit.se356.core.application.vehicle.query.GetVehicleByIdQuery;
import com.uit.se356.core.application.vehicle.result.VehicleResult;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.vehicle.VehicleId;
import com.uit.se356.core.presentation.dto.vehicle.VehicleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
  private final QueryBus queryBus;

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
            new UserId(request.shipperId()));
    VehicleResult result = commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.created(result, "Vehicle created successfully"));
  }

  @Operation(summary = "Update an existing Vehicle")
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<VehicleResult>> updateVehicle(
      @PathVariable("id") String id, @RequestBody VehicleRequest request) {

    SaveVehicleCommand command =
        new SaveVehicleCommand(
            new VehicleId(id),
            request.licensePlate(),
            request.type(),
            request.maxWeight(),
            request.maxVolume(),
            new UserId(request.shipperId()));
    VehicleResult result = commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.ok(result, "Vehicle updated successfully"));
  }

  @Operation(summary = "Delete an existing Vehicle")
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable("id") String id) {
    commandBus.dispatch(new DeleteVehicleCommand(new VehicleId(id)));
    return ResponseEntity.ok(ApiResponse.ok(null, "Vehicle deleted successfully"));
  }

  @Operation(summary = "Get Vehicle by ID")
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<VehicleResult>> getVehicleById(@PathVariable("id") String id) {
    GetVehicleByIdQuery query = new GetVehicleByIdQuery(new VehicleId(id));
    VehicleResult result = queryBus.dispatch(query);

    return ResponseEntity.ok(ApiResponse.ok(result, "Vehicle retrieved successfully"));
  }

  @Operation(summary = "Get all Vehicles with pagination and filtering")
  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<VehicleSummaryProjection>>> getAllVehicles(
      @ParameterObject SearchPageable pageable) {

    GetAllVehiclesQuery query = new GetAllVehiclesQuery(pageable);
    PageResponse<VehicleSummaryProjection> result = queryBus.dispatch(query);

    return ResponseEntity.ok(ApiResponse.ok(result, "Vehicles retrieved successfully"));
  }
}
