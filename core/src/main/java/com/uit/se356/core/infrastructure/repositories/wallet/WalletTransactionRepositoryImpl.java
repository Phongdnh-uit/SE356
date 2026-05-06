package com.uit.se356.core.infrastructure.repositories.wallet;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.common.utils.PageableUtil;
import com.uit.se356.core.application.wallet.port.WalletTransactionRepository;
import com.uit.se356.core.domain.entities.wallet.WalletTransaction;
import com.uit.se356.core.domain.vo.wallet.TransactionId;
import com.uit.se356.core.domain.vo.wallet.WalletId;
import com.uit.se356.core.infrastructure.persistence.entities.wallet.WalletTransactionJpaEntity;
import com.uit.se356.core.infrastructure.persistence.mappers.wallet.WalletTransactionPersistenceMapper;
import com.uit.se356.core.infrastructure.persistence.repositories.wallet.WalletJpaRepository;
import com.uit.se356.core.infrastructure.persistence.repositories.wallet.WalletTransactionJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletTransactionRepositoryImpl implements WalletTransactionRepository {
  private final WalletTransactionJpaRepository transactionJpaRepository;
  private final WalletJpaRepository walletJpaRepository;
  private final WalletTransactionPersistenceMapper transactionPersistenceMapper;

  @Override
  @Transactional
  public WalletTransaction save(WalletTransaction transaction) {
    WalletTransactionJpaEntity entity = transactionPersistenceMapper.toEntity(transaction);
    entity.setWallet(walletJpaRepository.getReferenceById(transaction.getWalletId().getValue()));
    WalletTransactionJpaEntity saved = transactionJpaRepository.save(entity);
    return transactionPersistenceMapper.toDomain(saved);
  }

  @Override
  public Optional<WalletTransaction> findById(TransactionId id) {
    return transactionJpaRepository
        .findById(id.getValue())
        .map(transactionPersistenceMapper::toDomain);
  }

  @Override
  public Optional<WalletTransaction> findByIdempotencyKey(String idempotencyKey) {
    return transactionJpaRepository
        .findByIdempotencyKey(idempotencyKey)
        .map(transactionPersistenceMapper::toDomain);
  }

  @Override
  public PageResponse<WalletTransaction> findAllByWalletId(
      WalletId walletId, SearchPageable pageable) {
    Pageable springPageable = PageableUtil.createPageable(pageable);
    Page<WalletTransactionJpaEntity> page =
        transactionJpaRepository.findByWalletIdOrderByCreatedAtDesc(
            walletId.getValue(), springPageable);

    return PageResponse.from(page, transactionPersistenceMapper::toDomain);
  }
}
