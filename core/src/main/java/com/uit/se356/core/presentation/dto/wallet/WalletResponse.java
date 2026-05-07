package com.uit.se356.core.presentation.dto.wallet;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WalletResponse {
  private String walletId;
  private BigDecimal balance;
  private String currency;
  private String formattedBalance;
}
