package com.uit.se356.core.application.wallet.port;

import com.uit.se356.core.domain.entities.wallet.Wallet;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.wallet.WalletId;
import java.util.Optional;

public interface WalletRepository {
  Wallet save(Wallet wallet);

  Optional<Wallet> findById(WalletId id);

  Optional<Wallet> findByUserId(UserId userId);

  Wallet update(Wallet wallet);
}
