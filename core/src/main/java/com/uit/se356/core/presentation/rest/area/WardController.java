package com.uit.se356.core.presentation.rest.area;

import com.uit.se356.common.dto.ApiResponse;
import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.common.services.CommandBus;
import com.uit.se356.common.services.QueryBus;
import com.uit.se356.core.application.area.command.CreateWardCommand;
import com.uit.se356.core.application.area.command.DeleteWardCommand;
import com.uit.se356.core.application.area.command.UpdateWardCommand;
import com.uit.se356.core.application.area.projections.WardSummaryProjection;
import com.uit.se356.core.application.area.query.GetWardByIdQuery;
import com.uit.se356.core.application.area.query.WardSummaryQuery;
import com.uit.se356.core.application.area.result.WardResult;
import com.uit.se356.core.domain.vo.area.ProvinceId;
import com.uit.se356.core.domain.vo.area.WardId;
import com.uit.se356.core.presentation.dto.area.UpdateWardRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Ward Management", description = "API quản lý xã/phường")
@RestController
@RequestMapping("/api/v1/admin/wards")
@RequiredArgsConstructor
public class WardController {

  private final CommandBus commandBus;
  private final QueryBus queryBus;

  @Operation(summary = "Create a new Ward (Cấp Xã)")
  @PostMapping
  public ResponseEntity<ApiResponse<WardResult>> createWard(
      @RequestBody CreateWardCommand command) {
    WardResult result = commandBus.dispatch(command);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created(result, "Ward created successfully"));
  }

  @Operation(summary = "Update an existing Ward (Cấp Xã)")
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<WardResult>> updateWard(
      @PathVariable String id, @RequestBody UpdateWardRequest request) {
    UpdateWardCommand command =
        new UpdateWardCommand(
            new WardId(id),
            request.code(),
            request.name(),
            new ProvinceId(request.provinceId()),
            request.type(),
            request.polygon());
    WardResult result = commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.ok(result, "Ward updated successfully"));
  }

  @Operation(summary = "Delete an existing Ward (Cấp Xã)")
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteWard(@PathVariable String id) {
    commandBus.dispatch(new DeleteWardCommand(new WardId(id)));
    return ResponseEntity.ok(ApiResponse.ok(null, "Ward deleted successfully"));
  }

  @Operation(summary = "Get Ward by ID (Cấp Xã)")
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<WardResult>> getWardById(@PathVariable("id") String id) {
    WardResult result = queryBus.dispatch(new GetWardByIdQuery(new WardId(id)));
    return ResponseEntity.ok(ApiResponse.ok(result, "Ward retrieved successfully"));
  }

  @Operation(
      summary = "Get list of Wards with pagination and filtering",
      description = "Hỗ trợ phân trang, sắp xếp và filter RSQL. Ví dụ: code==XA;name==*Hội*")
  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<WardSummaryProjection>>> getAllWards(
      @ParameterObject @ModelAttribute SearchPageable pageable) {
    WardSummaryQuery query = new WardSummaryQuery(pageable);
    PageResponse<WardSummaryProjection> result = queryBus.dispatch(query);
    return ResponseEntity.ok(ApiResponse.ok(result, "Wards retrieved successfully"));
  }
}
