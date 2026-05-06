package com.uit.se356.core.application.wallet.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.application.wallet.port.WalletRepository;
import com.uit.se356.core.application.wallet.query.GetMyWalletQuery;
import com.uit.se356.core.application.wallet.result.WalletResult;
import com.uit.se356.core.domain.entities.wallet.Wallet;
import com.uit.se356.core.domain.exception.WalletErrorCode;

public class GetMyWalletHandler implements QueryHandler<GetMyWalletQuery, WalletResult> {
  private final WalletRepository walletRepository;

  public GetMyWalletHandler(WalletRepository walletRepository) {
    this.walletRepository = walletRepository;
  }

  @Override
  public WalletResult handle(GetMyWalletQuery query) {
    Wallet wallet =
        walletRepository
            .findByUserId(query.userId())
            .orElseThrow(() -> new AppException(WalletErrorCode.WALLET_NOT_FOUND));

    return WalletResult.fromDomain(wallet);
  }
}
