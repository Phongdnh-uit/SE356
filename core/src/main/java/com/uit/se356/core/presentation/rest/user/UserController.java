package com.uit.se356.core.presentation.rest.user;

import com.uit.se356.common.dto.ApiResponse;
import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.security.UserPrincipal;
import com.uit.se356.common.services.CommandBus;
import com.uit.se356.common.services.QueryBus;
import com.uit.se356.common.utils.SecurityUtil;
import com.uit.se356.core.application.user.command.DeleteUserCommand;
import com.uit.se356.core.application.user.command.UpdateUserProfileCommand;
import com.uit.se356.core.application.user.command.UpdateUserRoleCommand;
import com.uit.se356.core.application.user.command.UpdateUserStatusCommand;
import com.uit.se356.core.application.user.projections.UserSummaryProjection;
import com.uit.se356.core.application.user.query.*;
import com.uit.se356.core.application.user.result.UserProfileResult;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import com.uit.se356.core.domain.vo.authentication.RoleId;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.authentication.UserStatus;
import com.uit.se356.core.presentation.dto.user.UpdateProfileRequest;
import com.uit.se356.core.presentation.dto.user.UpdateUserRoleRequest;
import com.uit.se356.core.presentation.dto.user.UpdateUserStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Profile")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final QueryBus queryBus;
  private final CommandBus commandBus;
  private final SecurityUtil<UserId> securityUtil;

  @Operation(summary = "Get My Profile")
  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserProfileResult>> getMyProfile() {
    UserId currentUserId =
        securityUtil
            .getCurrentUserPrincipal()
            .map(UserPrincipal::getId)
            .orElseThrow(() -> new AppException(AuthErrorCode.AUTHENTICATION_REQUIRED));

    GetUserProfileQuery query = new GetUserProfileQuery(currentUserId);
    UserProfileResult result = queryBus.dispatch(query);

    return ResponseEntity.ok(ApiResponse.ok(result, "Profile retrieved successfully"));
  }

  @Operation(summary = "Update My Profile")
  @PutMapping("/me")
  public ResponseEntity<ApiResponse<UserProfileResult>> updateMyProfile(
      @RequestBody UpdateProfileRequest request) {

    UserId currentUserId =
        securityUtil
            .getCurrentUserPrincipal()
            .map(UserPrincipal::getId)
            .orElseThrow(() -> new AppException(AuthErrorCode.AUTHENTICATION_REQUIRED));

    UpdateUserProfileCommand command =
        new UpdateUserProfileCommand(currentUserId, request.fullName());

    UserProfileResult result = commandBus.dispatch(command);

    return ResponseEntity.ok(ApiResponse.ok(result, "Profile updated successfully"));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponse<UserProfileResult>> getUserProfile(
      @PathVariable String userId) {
    GetUserProfileQuery query = new GetUserProfileQuery(new UserId(userId));
    UserProfileResult result = queryBus.dispatch(query);
    return ResponseEntity.ok(ApiResponse.ok(result, "Profile retrieved successfully"));
  }

  @GetMapping("/all")
  public ResponseEntity<ApiResponse<PageResponse<UserSummaryProjection>>> getAllUserProfiles(
      @ParameterObject SearchPageable pageable) {
    GetAllUserProfilesQuery query = new GetAllUserProfilesQuery(pageable);
    PageResponse<UserSummaryProjection> result = queryBus.dispatch(query);
    return ResponseEntity.ok(ApiResponse.ok(result, "All profiles retrieved successfully"));
  }

  @Operation(summary = "Get Users by Status with Pagination")
  @GetMapping("/status")
  public ResponseEntity<ApiResponse<PageResponse<UserSummaryProjection>>> getUsersByStatus(
      @RequestParam UserStatus status, SearchPageable pageable) {
    GetUsersByStatusQuery query = new GetUsersByStatusQuery(status, pageable);
    PageResponse<UserSummaryProjection> result = queryBus.dispatch(query);
    return ResponseEntity.ok(ApiResponse.ok(result, "Users retrieved successfully"));
  }

  @Operation(summary = "Update User Role")
  @PutMapping("/{userId}/role")
  public ResponseEntity<ApiResponse<UserProfileResult>> updateUserRole(
      @PathVariable String userId, @RequestBody UpdateUserRoleRequest request) {
    UpdateUserRoleCommand command =
        new UpdateUserRoleCommand(new UserId(userId), new RoleId(request.roleId()));
    UserProfileResult result = commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.ok(result, "User role updated successfully"));
  }

  @Operation(summary = "Update User Status")
  @PatchMapping("/{userId}/status")
  public ResponseEntity<ApiResponse<UserProfileResult>> updateUserStatus(
      @PathVariable String userId, @RequestBody UpdateUserStatusRequest request) {
    UpdateUserStatusCommand command =
        new UpdateUserStatusCommand(new UserId(userId), request.status());
    UserProfileResult result = commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.ok(result, "User status updated successfully"));
  }

  @Operation(summary = "Delete User")
  @DeleteMapping("/{userId}")
  public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
    DeleteUserCommand command = new DeleteUserCommand(new UserId(userId));
    commandBus.dispatch(command);
    return ResponseEntity.ok(ApiResponse.ok(null, "User deleted successfully"));
  }

  @Operation(summary = "Find User by Email")
  @GetMapping("/search/email")
  public ResponseEntity<ApiResponse<UserProfileResult>> findUserByEmail(
      @RequestParam String email) {
    FindUserByEmailQuery query = new FindUserByEmailQuery(email);
    UserProfileResult result = queryBus.dispatch(query);
    return ResponseEntity.ok(ApiResponse.ok(result, "User found successfully"));
  }

  @Operation(summary = "Find User by Phone")
  @GetMapping("/search/phone")
  public ResponseEntity<ApiResponse<UserProfileResult>> findUserByPhone(
      @RequestParam String phoneNumber) {
    FindUserByPhoneQuery query = new FindUserByPhoneQuery(phoneNumber);
    UserProfileResult result = queryBus.dispatch(query);
    return ResponseEntity.ok(ApiResponse.ok(result, "User found successfully"));
  }
}
