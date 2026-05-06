package com.uit.se356.core.infrastructure.persistence.entities.wallet;

import com.uit.se356.common.entity.BaseEntity;
import com.uit.se356.core.domain.vo.wallet.PaymentProvider;
import com.uit.se356.core.domain.vo.wallet.TransactionStatus;
import com.uit.se356.core.domain.vo.wallet.TransactionType;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "wallet_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionJpaEntity extends BaseEntity<String> {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id", nullable = false)
  private WalletJpaEntity wallet;

  @Column(name = "amount", nullable = false, precision = 19, scale = 4)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private TransactionType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private TransactionStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "provider", nullable = false)
  private PaymentProvider provider;

  @Column(name = "reference_id", length = 100)
  private String referenceId;

  @Column(name = "idempotency_key", length = 100)
  private String idempotencyKey;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "metadata")
  private String metadata;
}
