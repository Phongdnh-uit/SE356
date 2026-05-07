package com.uit.se356.core.application.wallet.port;

import com.uit.se356.core.domain.entities.wallet.Wallet;

public interface WalletNotificationPort {
  void notifyBalanceUpdate(Wallet wallet);
}
