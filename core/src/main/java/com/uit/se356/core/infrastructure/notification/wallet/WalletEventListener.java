package com.uit.se356.core.infrastructure.notification.wallet;

import com.uit.se356.common.services.CommandBus;
import com.uit.se356.core.application.wallet.command.CreateWalletCommand;
import com.uit.se356.core.domain.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletEventListener {
  private final CommandBus commandBus;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleUserCreatedEvent(UserCreatedEvent event) {
    log.info("Received UserCreatedEvent for user {}. Creating wallet...", event.userId());
    try {
      commandBus.dispatch(new CreateWalletCommand(event.userId()));
      log.info("Successfully created wallet for user {}", event.userId());
    } catch (Exception e) {
      log.error("Failed to create wallet for user {}", event.userId(), e);
      // Có thể thêm cơ chế retry hoặc thông báo lỗi ở đây
    }
  }
}
