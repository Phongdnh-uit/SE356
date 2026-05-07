package com.uit.se356.core.domain.vo.wallet;

import java.util.Objects;

public class EscrowId {
  private final String value;

  public EscrowId(String value) {
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
    EscrowId escrowId = (EscrowId) o;
    return Objects.equals(value, escrowId.value);
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
