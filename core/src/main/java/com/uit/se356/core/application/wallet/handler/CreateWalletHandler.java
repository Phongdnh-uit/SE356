package com.uit.se356.core.application.wallet.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.IdGenerator;
import com.uit.se356.core.application.wallet.command.CreateWalletCommand;
import com.uit.se356.core.application.wallet.port.WalletRepository;
import com.uit.se356.core.domain.entities.wallet.Wallet;
import com.uit.se356.core.domain.exception.WalletErrorCode;
import com.uit.se356.core.domain.vo.wallet.WalletId;

public class CreateWalletHandler implements CommandHandler<CreateWalletCommand, Void> {
  private final WalletRepository walletRepository;
  private final IdGenerator idGenerator;

  public CreateWalletHandler(WalletRepository walletRepository, IdGenerator idGenerator) {
    this.walletRepository = walletRepository;
    this.idGenerator = idGenerator;
  }

  @Override
  public Void handle(CreateWalletCommand command) {
    walletRepository
        .findByUserId(command.userId())
        .ifPresent(
            w -> {
              throw new AppException(WalletErrorCode.WALLET_ALREADY_EXISTS);
            });

    Wallet wallet =
        Wallet.create(new WalletId(idGenerator.generate().toString()), command.userId());
    walletRepository.save(wallet);
    return null;
  }
}
