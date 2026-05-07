package com.uit.se356.core.infrastructure.persistence.mappers.wallet;

import com.uit.se356.core.domain.entities.wallet.WalletEscrow;
import com.uit.se356.core.domain.vo.wallet.EscrowId;
import com.uit.se356.core.domain.vo.wallet.WalletId;
import com.uit.se356.core.infrastructure.persistence.entities.wallet.WalletEscrowJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WalletEscrowPersistenceMapper {

  public WalletEscrow toDomain(WalletEscrowJpaEntity entity) {
    if (entity == null) {
      return null;
    }
    return WalletEscrow.rehydrate(
        new EscrowId(entity.getId()),
        new WalletId(entity.getWallet().getId()),
        entity.getOrderId(),
        entity.getAmount(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  public WalletEscrowJpaEntity toEntity(WalletEscrow domain) {
    if (domain == null) {
      return null;
    }
    WalletEscrowJpaEntity entity = new WalletEscrowJpaEntity();
    entity.setId(domain.getId().getValue());
    entity.setOrderId(domain.getOrderId());
    entity.setAmount(domain.getAmount());
    entity.setStatus(domain.getStatus());
    return entity;
  }
}
