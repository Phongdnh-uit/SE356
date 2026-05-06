package com.uit.se356.core.application.wallet.result;

import java.util.Map;

/** Kết quả sau khi parse callback từ Payment Provider. */
public record PaymentCallbackResult(
    String transactionId,
    boolean success,
    String providerReferenceId,
    String rawResponse,
    Map<String, Object> metadata) {}
