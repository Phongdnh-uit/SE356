package com.uit.se356.core.domain.entities.wallet;

import com.uit.se356.core.domain.vo.wallet.EscrowId;
import com.uit.se356.core.domain.vo.wallet.EscrowStatus;
import com.uit.se356.core.domain.vo.wallet.WalletId;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class WalletEscrow {
  private final EscrowId id;
  private final WalletId walletId;
  private final String orderId;
  private final BigDecimal amount;
  private EscrowStatus status;
  private final Instant createdAt;
  private Instant updatedAt;

  private WalletEscrow(
      EscrowId id,
      WalletId walletId,
      String orderId,
      BigDecimal amount,
      EscrowStatus status,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.walletId = walletId;
    this.orderId = orderId;
    this.amount = amount;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // ============================ FACTORY ============================
  public static WalletEscrow create(
      EscrowId id, WalletId walletId, String orderId, BigDecimal amount) {
    Objects.requireNonNull(id);
    Objects.requireNonNull(walletId);
    Objects.requireNonNull(orderId);
    Objects.requireNonNull(amount);

    Instant now = Instant.now();
    return new WalletEscrow(id, walletId, orderId, amount, EscrowStatus.LOCKED, now, now);
  }

  public static WalletEscrow rehydrate(
      EscrowId id,
      WalletId walletId,
      String orderId,
      BigDecimal amount,
      EscrowStatus status,
      Instant createdAt,
      Instant updatedAt) {
    return new WalletEscrow(id, walletId, orderId, amount, status, createdAt, updatedAt);
  }

  // ============================ BEHAVIOR ============================
  public void updateStatus(EscrowStatus next) {
    Objects.requireNonNull(next);
    this.status = next;
    this.updatedAt = Instant.now();
  }

  // ============================ GETTERS ============================
  public EscrowId getId() {
    return id;
  }

  public WalletId getWalletId() {
    return walletId;
  }

  public String getOrderId() {
    return orderId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public EscrowStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
