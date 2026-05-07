package com.uit.se356.core.presentation.rest.auth;

import com.uit.se356.common.dto.ApiResponse;
import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.common.dto.SearchRequest;
import com.uit.se356.common.services.CommandBus;
import com.uit.se356.common.services.QueryBus;
import com.uit.se356.core.application.authentication.command.permission.AssignPermissionCommand;
import com.uit.se356.core.application.authentication.command.role.AssignUserRoleCommand;
import com.uit.se356.core.application.authentication.command.role.CreateRoleCommand;
import com.uit.se356.core.application.authentication.command.role.DeleteRoleCommand;
import com.uit.se356.core.application.authentication.command.role.UpdateRoleCommand;
import com.uit.se356.core.application.authentication.projections.PermissionSummaryProjection;
import com.uit.se356.core.application.authentication.projections.RoleSummaryProjection;
import com.uit.se356.core.application.authentication.query.role.GetPermissionsByRoleQuery;
import com.uit.se356.core.application.authentication.query.role.GetRoleByIdQuery;
import com.uit.se356.core.application.authentication.query.role.RoleSummaryQuery;
import com.uit.se356.core.application.authentication.result.RoleResult;
import com.uit.se356.core.domain.vo.authentication.PermissionId;
import com.uit.se356.core.domain.vo.authentication.RoleId;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.presentation.dto.role.AssignPermissionRequest;
import com.uit.se356.core.presentation.dto.role.UpdateRoleRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Role")
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@RestController
public class RoleController {
  private final CommandBus commandBus;
  private final QueryBus queryBus;

  @PostMapping
  public ResponseEntity<ApiResponse<RoleResult>> createRole(
      @RequestBody CreateRoleCommand command) {
    return ResponseEntity.ok(
        ApiResponse.ok(commandBus.dispatch(command), "Role created successfully"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<RoleResult>> updateRole(
      @PathVariable("id") String id, @RequestBody UpdateRoleRequest request) {
    UpdateRoleCommand command =
        new UpdateRoleCommand(id, request.getName(), request.getDescription(), request.isDefault());
    return ResponseEntity.ok(
        ApiResponse.ok(commandBus.dispatch(command), "Role updated successfully"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable("id") String id) {
    DeleteRoleCommand command = new DeleteRoleCommand(new RoleId(id));
    commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.ok(null, "Role deleted successfully"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<RoleSummaryProjection>>> getAllRoles(
      @ParameterObject @ModelAttribute SearchPageable query) {
    return ResponseEntity.ok(
        ApiResponse.ok(
            queryBus.dispatch(new RoleSummaryQuery(query)), "Roles retrieved successfully"));
  }

  @PostMapping("/{id}/permissions")
  public ResponseEntity<ApiResponse<Void>> assignPermissionsToRole(
      @PathVariable("id") String id, @RequestBody AssignPermissionRequest request) {
    AssignPermissionCommand command =
        new AssignPermissionCommand(
            new RoleId(id),
            request.getPermissionIds().stream().map(PermissionId::new).collect(Collectors.toSet()));
    return ResponseEntity.ok(
        ApiResponse.ok(commandBus.dispatch(command), "Permissions assigned to role successfully"));
  }

  @GetMapping("/{id}/permissions")
  public ResponseEntity<ApiResponse<Map<String, List<PermissionSummaryProjection>>>>
      getRolePermissions(
          @PathVariable("id") String id, @ParameterObject @ModelAttribute SearchRequest pageable) {
    return ResponseEntity.ok(
        ApiResponse.ok(
            queryBus.dispatch(new GetPermissionsByRoleQuery(new RoleId(id), pageable)),
            "Role permissions retrieved successfully"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<RoleResult>> getRoleById(@PathVariable("id") String id) {
    return ResponseEntity.ok(
        ApiResponse.ok(
            queryBus.dispatch(new GetRoleByIdQuery(new RoleId(id))),
            "Role retrieved successfully"));
  }

  @PostMapping("/{id}/assign/{userId}")
  public ResponseEntity<ApiResponse<Void>> assignRoleToUser(
      @PathVariable("id") String roleId, @PathVariable("userId") String userId) {
    AssignUserRoleCommand command =
        new AssignUserRoleCommand(new RoleId(roleId), new UserId(userId));
    commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.noContent("Role assigned to user successfully"));
  }
}
