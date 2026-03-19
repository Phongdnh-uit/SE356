package com.uit.se356.core.presentation.rest.user;

import com.uit.se356.common.dto.ApiResponse;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.security.UserPrincipal;
import com.uit.se356.common.services.CommandBus;
import com.uit.se356.common.services.QueryBus;
import com.uit.se356.common.utils.SecurityUtil;
import com.uit.se356.core.application.user.command.UpdateUserProfileCommand;
import com.uit.se356.core.application.user.query.GetUserProfileQuery;
import com.uit.se356.core.application.user.result.UserProfileResult;
import com.uit.se356.core.domain.exception.AuthErrorCode;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.presentation.dto.user.UpdateProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
}
