package com.uit.se356.core.infrastructure.persistence.entities.wallet;

import com.uit.se356.common.entity.BaseEntity;
import com.uit.se356.core.domain.vo.wallet.WalletStatus;
import com.uit.se356.core.infrastructure.persistence.entities.authentication.UserJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletJpaEntity extends BaseEntity<String> {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private UserJpaEntity user;

  @Column(name = "available_balance", nullable = false, precision = 19, scale = 4)
  private BigDecimal availableBalance = BigDecimal.ZERO;

  @Column(name = "locked_balance", nullable = false, precision = 19, scale = 4)
  private BigDecimal lockedBalance = BigDecimal.ZERO;

  @Column(name = "currency", nullable = false, length = 3)
  private String currency = "VND";

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private WalletStatus status;

  @Version
  @Column(name = "version", nullable = false)
  private Long version;
}
