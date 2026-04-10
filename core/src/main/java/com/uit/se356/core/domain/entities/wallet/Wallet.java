package com.uit.se356.core.domain.entities.wallet;

import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.wallet.WalletId;
import com.uit.se356.core.domain.vo.wallet.WalletStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class Wallet {
  private final WalletId id;
  private final UserId userId;
  private BigDecimal availableBalance;
  private BigDecimal lockedBalance;
  private String currency;
  private WalletStatus status;
  private Long version;
  private final Instant createdAt;
  private Instant updatedAt;

  private Wallet(
      WalletId id,
      UserId userId,
      BigDecimal availableBalance,
      BigDecimal lockedBalance,
      String currency,
      WalletStatus status,
      Long version,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.userId = userId;
    this.availableBalance = availableBalance;
    this.lockedBalance = lockedBalance;
    this.currency = currency;
    this.status = status;
    this.version = version;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // ============================ FACTORY ============================
  public static Wallet create(WalletId id, UserId userId) {
    Objects.requireNonNull(id);
    Objects.requireNonNull(userId);

    Instant now = Instant.now();
    return new Wallet(
        id, userId, BigDecimal.ZERO, BigDecimal.ZERO, "VND", WalletStatus.ACTIVE, 0L, now, now);
  }

  public static Wallet rehydrate(
      WalletId id,
      UserId userId,
      BigDecimal availableBalance,
      BigDecimal lockedBalance,
      String currency,
      WalletStatus status,
      Long version,
      Instant createdAt,
      Instant updatedAt) {
    return new Wallet(
        id,
        userId,
        availableBalance,
        lockedBalance,
        currency,
        status,
        version,
        createdAt,
        updatedAt);
  }

  // ============================ BEHAVIOR ============================
  public void updateStatus(WalletStatus next) {
    Objects.requireNonNull(next);
    this.status = next;
    this.updatedAt = Instant.now();
  }

  // ============================ GETTERS ============================
  public WalletId getId() {
    return id;
  }

  public UserId getUserId() {
    return userId;
  }

  public BigDecimal getAvailableBalance() {
    return availableBalance;
  }

  public BigDecimal getLockedBalance() {
    return lockedBalance;
  }

  public String getCurrency() {
    return currency;
  }

  public WalletStatus getStatus() {
    return status;
  }

  public Long getVersion() {
    return version;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
