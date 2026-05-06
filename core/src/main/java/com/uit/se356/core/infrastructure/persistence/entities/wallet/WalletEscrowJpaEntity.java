package com.uit.se356.core.infrastructure.persistence.entities.wallet;

import com.uit.se356.common.entity.BaseEntity;
import com.uit.se356.core.domain.vo.wallet.EscrowStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wallet_escrows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletEscrowJpaEntity extends BaseEntity<String> {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id", nullable = false)
  private WalletJpaEntity wallet;

  @Column(name = "order_id", nullable = false, length = 100)
  private String orderId;

  @Column(name = "amount", nullable = false, precision = 19, scale = 4)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private EscrowStatus status;
}
