package com.uit.se356.core.application.wallet.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.wallet.command.ProcessTopUpWebhookCommand;
import com.uit.se356.core.application.wallet.port.WalletRepository;
import com.uit.se356.core.application.wallet.port.WalletTransactionRepository;
import com.uit.se356.core.application.wallet.result.PaymentCallbackResult;
import com.uit.se356.core.application.wallet.strategies.PaymentProviderStrategy;
import com.uit.se356.core.domain.entities.wallet.Wallet;
import com.uit.se356.core.domain.entities.wallet.WalletTransaction;
import com.uit.se356.core.domain.exception.WalletErrorCode;
import com.uit.se356.core.domain.vo.wallet.TransactionId;
import com.uit.se356.core.domain.vo.wallet.TransactionStatus;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class ProcessTopUpWebhookHandler
    implements CommandHandler<ProcessTopUpWebhookCommand, Void> {
  private final WalletRepository walletRepository;
  private final WalletTransactionRepository transactionRepository;
  private final List<PaymentProviderStrategy> strategies;

  public ProcessTopUpWebhookHandler(
      WalletRepository walletRepository,
      WalletTransactionRepository transactionRepository,
      List<PaymentProviderStrategy> strategies) {
    this.walletRepository = walletRepository;
    this.transactionRepository = transactionRepository;
    this.strategies = strategies;
  }

  @Override
  @Transactional
  public Void handle(ProcessTopUpWebhookCommand command) {
    // 1. Tìm strategy phù hợp
    PaymentProviderStrategy strategy =
        strategies.stream()
            .filter(s -> s.supports(command.provider()))
            .findFirst()
            .orElseThrow(() -> new AppException(CommonErrorCode.FIELD_INVALID));

    // 2. Xác thực chữ ký
    if (!strategy.verifyCallback(command.params())) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, "Invalid signature");
    }

    // 3. Parse kết quả
    PaymentCallbackResult result = strategy.parseCallback(command.params());

    // 4. Kiểm tra transaction có tồn tại
    WalletTransaction transaction =
        transactionRepository
            .findById(new TransactionId(result.transactionId()))
            .orElseThrow(() -> new AppException(WalletErrorCode.TRANSACTION_NOT_FOUND));

    // 5. Kiểm tra tính idempotent (nếu đã SUCCESS hoặc FAILED thì bỏ qua)
    if (transaction.getStatus() != TransactionStatus.PENDING) {
      return null;
    }

    // 6. Lấy ví liên quan
    Wallet wallet =
        walletRepository
            .findById(transaction.getWalletId())
            .orElseThrow(() -> new AppException(WalletErrorCode.WALLET_NOT_FOUND));

    // 7. Cập nhật trạng thái giao dịch
    if (result.success()) {
      transaction.updateStatus(TransactionStatus.SUCCESS);
      // Nạp tiền vào số dư
      wallet.deposit(transaction.getAmount());
    } else {
      transaction.updateStatus(TransactionStatus.FAILED);
    }

    // 8. Lưu lại
    transactionRepository.save(transaction);
    walletRepository.update(wallet);

    return null;
  }
}
