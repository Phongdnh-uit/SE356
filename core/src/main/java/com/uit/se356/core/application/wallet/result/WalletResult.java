package com.uit.se356.core.application.wallet.result;

import com.uit.se356.core.domain.entities.wallet.Wallet;
import com.uit.se356.core.domain.vo.wallet.WalletStatus;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public record WalletResult(
    String walletId,
    BigDecimal balance,
    String currency,
    String formattedBalance,
    WalletStatus status) {
  public static WalletResult fromDomain(Wallet wallet) {
    NumberFormat vndFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
    return new WalletResult(
        wallet.getId().getValue(),
        wallet.getAvailableBalance(),
        wallet.getCurrency(),
        vndFormat.format(wallet.getAvailableBalance()),
        wallet.getStatus());
  }
}
