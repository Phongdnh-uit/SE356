package com.uit.se356.core.domain.entities.wallet;

import com.uit.se356.core.domain.vo.wallet.TransactionId;
import com.uit.se356.core.domain.vo.wallet.TransactionStatus;
import com.uit.se356.core.domain.vo.wallet.TransactionType;
import com.uit.se356.core.domain.vo.wallet.WalletId;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class WalletTransaction {
  private final TransactionId id;
  private final WalletId walletId;
  private final BigDecimal amount;
  private final TransactionType type;
  private TransactionStatus status;
  private final String referenceId;
  private final String idempotencyKey;
  private final String metadata;
  private final Instant createdAt;
  private Instant updatedAt;

  private WalletTransaction(
      TransactionId id,
      WalletId walletId,
      BigDecimal amount,
      TransactionType type,
      TransactionStatus status,
      String referenceId,
      String idempotencyKey,
      String metadata,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.walletId = walletId;
    this.amount = amount;
    this.type = type;
    this.status = status;
    this.referenceId = referenceId;
    this.idempotencyKey = idempotencyKey;
    this.metadata = metadata;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // ============================ FACTORY ============================
  public static WalletTransaction create(
      TransactionId id,
      WalletId walletId,
      BigDecimal amount,
      TransactionType type,
      String referenceId,
      String idempotencyKey,
      String metadata) {
    Objects.requireNonNull(id);
    Objects.requireNonNull(walletId);
    Objects.requireNonNull(amount);
    Objects.requireNonNull(type);

    Instant now = Instant.now();
    return new WalletTransaction(
        id,
        walletId,
        amount,
        type,
        TransactionStatus.PENDING,
        referenceId,
        idempotencyKey,
        metadata,
        now,
        now);
  }

  public static WalletTransaction rehydrate(
      TransactionId id,
      WalletId walletId,
      BigDecimal amount,
      TransactionType type,
      TransactionStatus status,
      String referenceId,
      String idempotencyKey,
      String metadata,
      Instant createdAt,
      Instant updatedAt) {
    return new WalletTransaction(
        id,
        walletId,
        amount,
        type,
        status,
        referenceId,
        idempotencyKey,
        metadata,
        createdAt,
        updatedAt);
  }

  // ============================ BEHAVIOR ============================
  public void updateStatus(TransactionStatus next) {
    Objects.requireNonNull(next);
    this.status = next;
    this.updatedAt = Instant.now();
  }

  // ============================ GETTERS ============================
  public TransactionId getId() {
    return id;
  }

  public WalletId getWalletId() {
    return walletId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public TransactionType getType() {
    return type;
  }

  public TransactionStatus getStatus() {
    return status;
  }

  public String getReferenceId() {
    return referenceId;
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public String getMetadata() {
    return metadata;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
