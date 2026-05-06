package com.uit.se356.core.presentation.rest.wallet;

import com.uit.se356.common.dto.ApiResponse;
import com.uit.se356.common.services.CommandBus;
import com.uit.se356.common.services.QueryBus;
import com.uit.se356.common.utils.SecurityUtil;
import com.uit.se356.core.application.wallet.command.TopUpCommand;
import com.uit.se356.core.application.wallet.query.GetMyWalletQuery;
import com.uit.se356.core.application.wallet.result.WalletResult;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.presentation.dto.wallet.TopUpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Wallet")
@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {
  private final CommandBus commandBus;
  private final QueryBus queryBus;
  private final SecurityUtil<UserId> securityUtil;

  @Operation(summary = "Get My Wallet")
  @GetMapping("/me")
  public ResponseEntity<ApiResponse<WalletResult>> getMyWallet() {
    UserId userId = securityUtil.getCurrentUserPrincipal().get().getId();
    WalletResult result = queryBus.dispatch(new GetMyWalletQuery(userId));
    return ResponseEntity.ok(ApiResponse.ok(result, "Wallet retrieved successfully"));
  }

  @Operation(summary = "Top Up Wallet")
  @PostMapping("/top-up")
  public ResponseEntity<ApiResponse<String>> topUp(@Valid @RequestBody TopUpRequest request) {
    UserId userId = securityUtil.getCurrentUserPrincipal().get().getId();
    String paymentUrl =
        commandBus.dispatch(new TopUpCommand(userId, request.getAmount(), request.getProvider()));
    return ResponseEntity.ok(ApiResponse.ok(paymentUrl, "Top-up initiated successfully"));
  }
}
