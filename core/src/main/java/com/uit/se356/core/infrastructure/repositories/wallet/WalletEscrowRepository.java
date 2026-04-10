package com.uit.se356.core.infrastructure.repositories.wallet;

import com.uit.se356.common.repository.CommonRepository;
import com.uit.se356.core.infrastructure.persistence.entities.wallet.WalletEscrowJpaEntity;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletEscrowRepository extends CommonRepository<WalletEscrowJpaEntity, String> {
  Optional<WalletEscrowJpaEntity> findByOrderId(String orderId);
}
