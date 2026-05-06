package com.uit.se356.core.presentation.rest.wallet;

import com.uit.se356.common.services.CommandBus;
import com.uit.se356.core.application.wallet.command.ProcessTopUpWebhookCommand;
import com.uit.se356.core.domain.vo.wallet.PaymentProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Wallet Webhook")
@RestController
@RequestMapping("/api/v1/wallet/webhook")
@RequiredArgsConstructor
public class WalletWebhookController {
  private final CommandBus commandBus;

  @PostMapping("/momo")
  public ResponseEntity<Void> handleMomoWebhook(@RequestBody Map<String, Object> payload) {
    ProcessTopUpWebhookCommand command =
        new ProcessTopUpWebhookCommand(PaymentProvider.MOMO, payload);
    commandBus.dispatch(command);
    return ResponseEntity.noContent().build();
  }
}
