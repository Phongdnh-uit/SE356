package com.uit.se356.core.domain.vo.wallet;

import java.util.Objects;

public class WalletId {
  private final String value;

  public WalletId(String value) {
    Objects.requireNonNull(value);
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WalletId walletId = (WalletId) o;
    return Objects.equals(value, walletId.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value;
  }
}
