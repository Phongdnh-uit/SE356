package com.uit.se356.core.application.wallet.handler;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.common.utils.IdGenerator;
import com.uit.se356.core.application.wallet.command.PayWithWalletCommand;
import com.uit.se356.core.application.wallet.port.WalletNotificationPort;
import com.uit.se356.core.application.wallet.port.WalletRepository;
import com.uit.se356.core.application.wallet.port.WalletTransactionRepository;
import com.uit.se356.core.domain.entities.wallet.Wallet;
import com.uit.se356.core.domain.entities.wallet.WalletTransaction;
import com.uit.se356.core.domain.exception.WalletErrorCode;
import com.uit.se356.core.domain.vo.wallet.PaymentProvider;
import com.uit.se356.core.domain.vo.wallet.TransactionId;
import com.uit.se356.core.domain.vo.wallet.TransactionStatus;
import com.uit.se356.core.domain.vo.wallet.TransactionType;
import org.springframework.transaction.annotation.Transactional;

public class PayWithWalletHandler implements CommandHandler<PayWithWalletCommand, Void> {
  private final WalletRepository walletRepository;
  private final WalletTransactionRepository transactionRepository;
  private final WalletNotificationPort notificationPort;
  private final IdGenerator idGenerator;

  public PayWithWalletHandler(
      WalletRepository walletRepository,
      WalletTransactionRepository transactionRepository,
      WalletNotificationPort notificationPort,
      IdGenerator idGenerator) {
    this.walletRepository = walletRepository;
    this.transactionRepository = transactionRepository;
    this.notificationPort = notificationPort;
    this.idGenerator = idGenerator;
  }

  @Override
  @Transactional
  public Void handle(PayWithWalletCommand command) {
    Wallet wallet =
        walletRepository
            .findByUserId(command.userId())
            .orElseThrow(() -> new AppException(WalletErrorCode.WALLET_NOT_FOUND));

    // Logic nghiệp vụ: trừ tiền trong domain
    wallet.pay(command.amount());

    // Tạo giao dịch
    WalletTransaction transaction =
        WalletTransaction.create(
            new TransactionId(idGenerator.generate().toString()),
            wallet.getId(),
            command.amount(),
            TransactionType.PAYMENT,
            PaymentProvider.SYSTEM,
            command.referenceId(),
            null, // No idempotency key for internal payment yet
            command.metadata());
    transaction.updateStatus(TransactionStatus.SUCCESS);

    // Lưu dữ liệu
    walletRepository.update(wallet);
    transactionRepository.save(transaction);

    // Thông báo cập nhật số dư qua WebSocket
    notificationPort.notifyBalanceUpdate(wallet);

    return null;
  }
}
