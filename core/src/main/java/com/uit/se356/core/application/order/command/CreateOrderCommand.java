package com.uit.se356.core.application.order.command;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.dto.FieldError;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.application.order.result.OrderResult;
import com.uit.se356.core.domain.vo.order.OrderType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record CreateOrderCommand(
    String trackingCode,
    OrderType type,
    String customerId,
    String senderId,
    String senderName,
    String senderPhone,
    String senderAddress,
    String senderWardId,
    String senderProvinceId,
    String recipientName,
    String recipientPhone,
    String recipientAddress,
    String recipientWardId,
    String recipientProvinceId,
    String description,
    Float weight,
    Double dimLength,
    Double dimWidth,
    Double dimHeight,
    BigDecimal valueDeclared,
    boolean fragile,
    boolean requiresSignature,
    BigDecimal shippingFee,
    BigDecimal insuranceFee)
    implements Command<OrderResult> {
  public CreateOrderCommand {
    List<FieldError> errors = new ArrayList<>();
    if (trackingCode == null || trackingCode.isBlank()) {
      errors.add(
          new FieldError(
              "trackingCode",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"trackingCode"}));
    }
    if (customerId == null || customerId.isBlank()) {
      errors.add(
          new FieldError(
              "customerId",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"customerId"}));
    }
    if (senderId == null || senderId.isBlank()) {
      errors.add(
          new FieldError(
              "senderId",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"senderId"}));
    }
    if (senderName == null || senderName.isBlank()) {
      errors.add(
          new FieldError(
              "senderName",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"senderName"}));
    }
    if (senderPhone == null || senderPhone.isBlank()) {
      errors.add(
          new FieldError(
              "senderPhone",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"senderPhone"}));
    }
    if (senderAddress == null || senderAddress.isBlank()) {
      errors.add(
          new FieldError(
              "senderAddress",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"senderAddress"}));
    }
    if (senderWardId == null || senderWardId.isBlank()) {
      errors.add(
          new FieldError(
              "senderWardId",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"senderWardId"}));
    }
    if (senderProvinceId == null || senderProvinceId.isBlank()) {
      errors.add(
          new FieldError(
              "senderProvinceId",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"senderProvinceId"}));
    }
    if (recipientName == null || recipientName.isBlank()) {
      errors.add(
          new FieldError(
              "recipientName",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"recipientName"}));
    }
    if (recipientPhone == null || recipientPhone.isBlank()) {
      errors.add(
          new FieldError(
              "recipientPhone",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"recipientPhone"}));
    }
    if (recipientAddress == null || recipientAddress.isBlank()) {
      errors.add(
          new FieldError(
              "recipientAddress",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"recipientAddress"}));
    }
    if (recipientWardId == null || recipientWardId.isBlank()) {
      errors.add(
          new FieldError(
              "recipientWardId",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"recipientWardId"}));
    }
    if (recipientProvinceId == null || recipientProvinceId.isBlank()) {
      errors.add(
          new FieldError(
              "recipientProvinceId",
              CommonErrorCode.FIELD_REQUIRED.getMessageKey(),
              new Object[] {"recipientProvinceId"}));
    }
    if (weight == null || weight <= 0) {
      errors.add(
          new FieldError(
              "weight",
              CommonErrorCode.FIELD_INVALID.getMessageKey(),
              new Object[] {"weight must"}));
    }
    if (dimLength == null || dimLength <= 0) {
      errors.add(
          new FieldError(
              "dimLength",
              CommonErrorCode.FIELD_INVALID.getMessageKey(),
              new Object[] {"dimLength must"}));
    }
    if (dimWidth == null || dimWidth <= 0) {
      errors.add(
          new FieldError(
              "dimWidth",
              CommonErrorCode.FIELD_INVALID.getMessageKey(),
              new Object[] {"dimWidth must"}));
    }
    if (dimHeight == null || dimHeight <= 0) {
      errors.add(
          new FieldError(
              "dimHeight",
              CommonErrorCode.FIELD_INVALID.getMessageKey(),
              new Object[] {"dimHeight must"}));
    }
    if (valueDeclared == null || valueDeclared.compareTo(BigDecimal.ZERO) < 0) {
      errors.add(
          new FieldError(
              "valueDeclared",
              CommonErrorCode.FIELD_INVALID.getMessageKey(),
              new Object[] {"valueDeclared must"}));
    }
    if (shippingFee == null || shippingFee.compareTo(BigDecimal.ZERO) < 0) {
      errors.add(
          new FieldError(
              "shippingFee",
              CommonErrorCode.FIELD_INVALID.getMessageKey(),
              new Object[] {"shippingFee must"}));
    }
    if (insuranceFee == null || insuranceFee.compareTo(BigDecimal.ZERO) < 0) {
      errors.add(
          new FieldError(
              "insuranceFee",
              CommonErrorCode.FIELD_INVALID.getMessageKey(),
              new Object[] {"insuranceFee must"}));
    }
    if (!errors.isEmpty()) {
      throw new AppException(CommonErrorCode.VALIDATION_ERROR, errors);
    }
  }
}
