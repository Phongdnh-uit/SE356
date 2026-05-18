package com.uit.se356.core.infrastructure.notification.wallet;

import com.uit.se356.core.application.wallet.port.WalletNotificationPort;
import com.uit.se356.core.domain.entities.wallet.Wallet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DummyWalletNotificationPort implements WalletNotificationPort {
  @Override
  public void notifyBalanceUpdate(Wallet wallet) {
    log.info("Wallet {} balance updated: {}", wallet.getId(), wallet.getAvailableBalance());
  }
}
