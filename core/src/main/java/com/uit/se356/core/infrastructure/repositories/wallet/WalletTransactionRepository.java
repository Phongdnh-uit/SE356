package com.uit.se356.core.infrastructure.repositories.wallet;

import com.uit.se356.common.repository.CommonRepository;
import com.uit.se356.core.infrastructure.persistence.entities.wallet.WalletTransactionJpaEntity;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTransactionRepository
    extends CommonRepository<WalletTransactionJpaEntity, String> {
  Optional<WalletTransactionJpaEntity> findByIdempotencyKey(String idempotencyKey);
}
