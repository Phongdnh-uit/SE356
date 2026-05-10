package com.uit.se356.core.presentation.dto.wallet;

import com.uit.se356.core.domain.vo.wallet.TransactionStatus;
import com.uit.se356.core.domain.vo.wallet.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WalletTransactionResponse {
  private String transactionId;
  private BigDecimal amount;
  private TransactionType type;
  private TransactionStatus status;
  private String referenceId;
  private String color;
  private Instant createdAt;
}
