package com.uit.se356.core.domain.entities.wallet;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.core.domain.exception.WalletErrorCode;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.wallet.WalletId;
import com.uit.se356.core.domain.vo.wallet.WalletStatus;
import java.math.BigDecimal;
import java.util.Objects;

public class Wallet {
  private final WalletId id;
  private final UserId userId;
  private BigDecimal availableBalance;
  private BigDecimal lockedBalance;
  private String currency;
  private WalletStatus status;

  private Wallet(
      WalletId id,
      UserId userId,
      BigDecimal availableBalance,
      BigDecimal lockedBalance,
      String currency,
      WalletStatus status) {
    this.id = id;
    this.userId = userId;
    this.availableBalance = availableBalance;
    this.lockedBalance = lockedBalance;
    this.currency = currency;
    this.status = status;
  }

  // ============================ FACTORY ============================
  public static Wallet create(WalletId id, UserId userId) {
    Objects.requireNonNull(id);
    Objects.requireNonNull(userId);

    return new Wallet(id, userId, BigDecimal.ZERO, BigDecimal.ZERO, "VND", WalletStatus.ACTIVE);
  }

  public static Wallet rehydrate(
      WalletId id,
      UserId userId,
      BigDecimal availableBalance,
      BigDecimal lockedBalance,
      String currency,
      WalletStatus status) {
    return new Wallet(id, userId, availableBalance, lockedBalance, currency, status);
  }

  // ============================ BEHAVIOR ============================
  public void updateStatus(WalletStatus next) {
    Objects.requireNonNull(next);
    this.status = next;
  }

  public void deposit(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new AppException(WalletErrorCode.INVALID_AMOUNT);
    }
    this.availableBalance = this.availableBalance.add(amount);
  }

  public void pay(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new AppException(WalletErrorCode.INVALID_AMOUNT);
    }
    if (this.availableBalance.compareTo(amount) < 0) {
      throw new AppException(WalletErrorCode.INSUFFICIENT_BALANCE);
    }
    this.availableBalance = this.availableBalance.subtract(amount);
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
}
