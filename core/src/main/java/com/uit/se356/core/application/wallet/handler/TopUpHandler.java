package com.uit.se356.core.application.wallet.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.IdGenerator;
import com.uit.se356.core.application.wallet.command.TopUpCommand;
import com.uit.se356.core.application.wallet.port.WalletRepository;
import com.uit.se356.core.application.wallet.port.WalletTransactionRepository;
import com.uit.se356.core.application.wallet.strategies.PaymentProviderStrategy;
import com.uit.se356.core.domain.entities.wallet.Wallet;
import com.uit.se356.core.domain.entities.wallet.WalletTransaction;
import com.uit.se356.core.domain.exception.WalletErrorCode;
import com.uit.se356.core.domain.vo.wallet.TransactionId;
import com.uit.se356.core.domain.vo.wallet.TransactionType;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class TopUpHandler implements CommandHandler<TopUpCommand, String> {
  private final WalletRepository walletRepository;
  private final WalletTransactionRepository transactionRepository;
  private final List<PaymentProviderStrategy> strategies;
  private final IdGenerator idGenerator;

  public TopUpHandler(
      WalletRepository walletRepository,
      WalletTransactionRepository transactionRepository,
      List<PaymentProviderStrategy> strategies,
      IdGenerator idGenerator) {
    this.walletRepository = walletRepository;
    this.transactionRepository = transactionRepository;
    this.strategies = strategies;
    this.idGenerator = idGenerator;
  }

  @Override
  @Transactional
  public String handle(TopUpCommand command) {
    Wallet wallet =
        walletRepository
            .findByUserId(command.userId())
            .orElseThrow(() -> new AppException(WalletErrorCode.WALLET_NOT_FOUND));

    // Tìm strategy phù hợp
    PaymentProviderStrategy strategy =
        strategies.stream()
            .filter(s -> s.supports(command.provider()))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("Unsupported provider: " + command.provider()));

    // Tạo giao dịch PENDING
    String transactionId = idGenerator.generate().toString();
    WalletTransaction transaction =
        WalletTransaction.create(
            new TransactionId(transactionId),
            wallet.getId(),
            command.amount(),
            TransactionType.DEPOSIT,
            command.provider(),
            null, // referenceId từ provider sẽ cập nhật sau khi có webhook
            null, // idempotencyKey
            null // metadata
            );

    transactionRepository.save(transaction);

    // Sử dụng Strategy tương ứng với Provider để lấy Payment URL
    return strategy.createPaymentUrl(transaction);
  }
}
