package com.uit.se356.core.presentation.dto.order;

public record UpdateRecipientRequest(
    String recipientName, String recipientPhone, String recipientAddress) {}
