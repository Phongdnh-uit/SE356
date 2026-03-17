package com.uit.se356.core.presentation.rest.depot;

import com.uit.se356.common.dto.ApiResponse;
import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.common.services.CommandBus;
import com.uit.se356.common.services.QueryBus;
import com.uit.se356.core.application.depot.command.CreateDepotCommand;
import com.uit.se356.core.application.depot.command.DeleteDepotCommand;
import com.uit.se356.core.application.depot.command.UpdateDepotCommand;
import com.uit.se356.core.application.depot.projection.DepotSummaryProjection;
import com.uit.se356.core.application.depot.query.GetAllDepotsQuery;
import com.uit.se356.core.application.depot.query.GetDepotByIdQuery;
import com.uit.se356.core.application.depot.result.DepotResult;
import com.uit.se356.core.domain.vo.depot.DepotId;
import com.uit.se356.core.presentation.dto.depot.UpdateDepotRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Depot Management")
@RestController
@RequestMapping("/api/v1/admin/depots")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('ADMIN')")
public class DepotController {

  private final CommandBus commandBus;
  private final QueryBus queryBus;

  @Operation(summary = "Create a new Depot")
  @PostMapping
  public ResponseEntity<ApiResponse<DepotResult>> createDepot(
      @RequestBody CreateDepotCommand command) {
    DepotResult result = commandBus.dispatch(command);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created(result, "Depot created successfully"));
  }

  @Operation(summary = "Get Depot by ID")
  @GetMapping("/{depotId}")
  public ResponseEntity<ApiResponse<DepotResult>> getDepotById(
      @PathVariable("depotId") String depotId) {
    GetDepotByIdQuery query = new GetDepotByIdQuery(new DepotId(depotId));
    DepotResult result = queryBus.dispatch(query);
    return ResponseEntity.ok(ApiResponse.ok(result, "Depot retrieved successfully"));
  }

  @Operation(summary = "Get all Depots with pagination and filtering")
  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<DepotSummaryProjection>>> getAllDepots(
      @ParameterObject @ModelAttribute SearchPageable pageable) {
    GetAllDepotsQuery query = new GetAllDepotsQuery(pageable);
    PageResponse<DepotSummaryProjection> result = queryBus.dispatch(query);
    return ResponseEntity.ok(ApiResponse.ok(result, "Depots retrieved successfully"));
  }

  @Operation(summary = "Update an existing Depot")
  @PutMapping("/{depotId}")
  public ResponseEntity<ApiResponse<DepotResult>> updateDepot(
      @PathVariable("depotId") String depotId, @RequestBody UpdateDepotRequest request) {

    UpdateDepotCommand command =
        new UpdateDepotCommand(
            new DepotId(depotId),
            request.name(),
            request.type(),
            request.latitude(),
            request.longitude());
    DepotResult result = commandBus.dispatch(command);

    return ResponseEntity.ok(ApiResponse.ok(result, "Depot updated successfully"));
  }

  @Operation(summary = "Delete a Depot")
  @DeleteMapping("/{depotId}")
  public ResponseEntity<ApiResponse<Void>> deleteDepot(@PathVariable("depotId") String depotId) {
    commandBus.dispatch(new DeleteDepotCommand(new DepotId(depotId)));
    return ResponseEntity.ok(ApiResponse.ok(null, "Depot deleted successfully"));
  }
}
