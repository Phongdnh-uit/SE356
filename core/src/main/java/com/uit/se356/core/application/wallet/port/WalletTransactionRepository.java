package com.uit.se356.core.application.wallet.port;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.core.domain.entities.wallet.WalletTransaction;
import com.uit.se356.core.domain.vo.wallet.TransactionId;
import com.uit.se356.core.domain.vo.wallet.WalletId;
import java.util.Optional;

public interface WalletTransactionRepository {
  WalletTransaction save(WalletTransaction transaction);

  Optional<WalletTransaction> findById(TransactionId id);

  Optional<WalletTransaction> findByIdempotencyKey(String idempotencyKey);

  PageResponse<WalletTransaction> findAllByWalletId(WalletId walletId, SearchPageable pageable);
}
