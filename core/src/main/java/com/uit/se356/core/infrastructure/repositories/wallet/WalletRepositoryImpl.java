package com.uit.se356.core.infrastructure.repositories.wallet;

import com.uit.se356.core.application.wallet.port.WalletRepository;
import com.uit.se356.core.domain.entities.wallet.Wallet;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.wallet.WalletId;
import com.uit.se356.core.infrastructure.persistence.entities.wallet.WalletJpaEntity;
import com.uit.se356.core.infrastructure.persistence.mappers.wallet.WalletPersistenceMapper;
import com.uit.se356.core.infrastructure.persistence.repositories.authentication.UserJpaRepository;
import com.uit.se356.core.infrastructure.persistence.repositories.wallet.WalletJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletRepositoryImpl implements WalletRepository {
  private final WalletJpaRepository walletJpaRepository;
  private final UserJpaRepository userJpaRepository;
  private final WalletPersistenceMapper walletPersistenceMapper;

  @Override
  @Transactional
  public Wallet save(Wallet wallet) {
    WalletJpaEntity entity = walletPersistenceMapper.toEntity(wallet);
    entity.setUser(userJpaRepository.getReferenceById(wallet.getUserId().value()));
    WalletJpaEntity saved = walletJpaRepository.save(entity);
    return walletPersistenceMapper.toDomain(saved);
  }

  @Override
  public Optional<Wallet> findById(WalletId id) {
    return walletJpaRepository
        .findByIdWithUser(id.getValue())
        .map(walletPersistenceMapper::toDomain);
  }

  @Override
  public Optional<Wallet> findByUserId(UserId userId) {
    return walletJpaRepository
        .findOne((root, query, cb) -> cb.equal(root.get("user").get("id"), userId.value()))
        .map(walletPersistenceMapper::toDomain);
  }

  @Override
  @Transactional
  public Wallet update(Wallet wallet) {
    WalletJpaEntity entity =
        walletJpaRepository
            .findById(wallet.getId().getValue())
            .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

    walletPersistenceMapper.updateFromDomain(wallet, entity);
    return walletPersistenceMapper.toDomain(entity);
  }
}
