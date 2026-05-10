package com.uit.se356.core.infrastructure.persistence.repositories.wallet;

import com.uit.se356.common.repository.CommonRepository;
import com.uit.se356.core.infrastructure.persistence.entities.wallet.WalletJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletJpaRepository extends CommonRepository<WalletJpaEntity, String> {

  @Query("SELECT w FROM WalletJpaEntity w JOIN FETCH w.user WHERE w.id = :id")
  Optional<WalletJpaEntity> findByIdWithUser(@Param("id") String id);
}
