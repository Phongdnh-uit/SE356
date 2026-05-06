package com.uit.se356.core.presentation.dto.wallet;

import com.uit.se356.core.domain.vo.wallet.PaymentProvider;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopUpRequest {
  @NotNull
  @Min(10000)
  private BigDecimal amount;

  @NotNull private PaymentProvider provider;
}
