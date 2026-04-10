package com.uit.se356.core.infrastructure.repositories.wallet;

import com.uit.se356.common.repository.CommonRepository;
import com.uit.se356.core.infrastructure.persistence.entities.wallet.WalletJpaEntity;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends CommonRepository<WalletJpaEntity, String> {
  Optional<WalletJpaEntity> findByUserId(String userId);
}
