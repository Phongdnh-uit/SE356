package com.uit.se356.core.infrastructure.persistence.mappers.wallet;

import com.uit.se356.core.domain.entities.wallet.WalletTransaction;
import com.uit.se356.core.domain.vo.wallet.TransactionId;
import com.uit.se356.core.domain.vo.wallet.WalletId;
import com.uit.se356.core.infrastructure.persistence.entities.wallet.WalletTransactionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WalletTransactionPersistenceMapper {

  public WalletTransaction toDomain(WalletTransactionJpaEntity entity) {
    if (entity == null) {
      return null;
    }
    return WalletTransaction.rehydrate(
        new TransactionId(entity.getId()),
        new WalletId(entity.getWallet().getId()),
        entity.getAmount(),
        entity.getType(),
        entity.getStatus(),
        entity.getProvider(),
        entity.getReferenceId(),
        entity.getIdempotencyKey(),
        entity.getMetadata(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  public WalletTransactionJpaEntity toEntity(WalletTransaction domain) {
    if (domain == null) {
      return null;
    }
    WalletTransactionJpaEntity entity = new WalletTransactionJpaEntity();
    entity.setId(domain.getId().getValue());
    entity.setAmount(domain.getAmount());
    entity.setType(domain.getType());
    entity.setStatus(domain.getStatus());
    entity.setProvider(domain.getProvider());
    entity.setReferenceId(domain.getReferenceId());
    entity.setIdempotencyKey(domain.getIdempotencyKey());
    entity.setMetadata(domain.getMetadata());
    return entity;
  }
}
