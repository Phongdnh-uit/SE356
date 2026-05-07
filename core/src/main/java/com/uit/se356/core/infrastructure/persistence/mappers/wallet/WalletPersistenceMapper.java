package com.uit.se356.core.infrastructure.persistence.mappers.wallet;

import com.uit.se356.core.domain.entities.wallet.Wallet;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.wallet.WalletId;
import com.uit.se356.core.infrastructure.persistence.entities.wallet.WalletJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WalletPersistenceMapper {

  public Wallet toDomain(WalletJpaEntity entity) {
    if (entity == null) {
      return null;
    }
    return Wallet.rehydrate(
        new WalletId(entity.getId()),
        new UserId(entity.getUser().getId()),
        entity.getAvailableBalance(),
        entity.getLockedBalance(),
        entity.getCurrency(),
        entity.getStatus());
  }

  public WalletJpaEntity toEntity(Wallet domain) {
    if (domain == null) {
      return null;
    }
    WalletJpaEntity entity = new WalletJpaEntity();
    entity.setId(domain.getId().getValue());
    entity.setAvailableBalance(domain.getAvailableBalance());
    entity.setLockedBalance(domain.getLockedBalance());
    entity.setCurrency(domain.getCurrency());
    entity.setStatus(domain.getStatus());
    return entity;
  }

  public void updateFromDomain(Wallet domain, WalletJpaEntity entity) {
    if (domain == null || entity == null) {
      return;
    }
    entity.setAvailableBalance(domain.getAvailableBalance());
    entity.setLockedBalance(domain.getLockedBalance());
    entity.setCurrency(domain.getCurrency());
    entity.setStatus(domain.getStatus());
  }
}
