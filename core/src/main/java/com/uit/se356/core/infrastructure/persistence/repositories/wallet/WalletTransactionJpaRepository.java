package com.uit.se356.core.infrastructure.persistence.repositories.wallet;

import com.uit.se356.common.repository.CommonRepository;
import com.uit.se356.core.infrastructure.persistence.entities.wallet.WalletTransactionJpaEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTransactionJpaRepository
    extends CommonRepository<WalletTransactionJpaEntity, String> {
  Optional<WalletTransactionJpaEntity> findByIdempotencyKey(String idempotencyKey);

  Page<WalletTransactionJpaEntity> findByWalletIdOrderByCreatedAtDesc(
      String walletId, Pageable pageable);
}
