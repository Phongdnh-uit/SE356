package com.uit.se356.core.infrastructure.persistence.mappers.order;

import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.core.domain.entities.order.Order;
import com.uit.se356.core.domain.vo.area.ProvinceId;
import com.uit.se356.core.domain.vo.area.WardId;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.domain.vo.order.Dimensions;
import com.uit.se356.core.domain.vo.order.OrderId;
import com.uit.se356.core.infrastructure.persistence.entities.order.OrderJpaEntity;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class OrderPersistenceMapper {

  private final ObjectMapper objectMapper;

  public OrderPersistenceMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public OrderJpaEntity toEntity(Order domain) {
    OrderJpaEntity entity = new OrderJpaEntity();
    entity.setId(domain.getId().value());
    entity.setTrackingCode(domain.getTrackingCode());
    entity.setType(domain.getType());
    entity.setStatus(domain.getStatus());

    entity.setCustomerId(domain.getCustomerId().value());
    entity.setSenderId(domain.getSenderId().value());
    entity.setSenderName(domain.getSenderName());
    entity.setSenderPhone(domain.getSenderPhone());
    entity.setSenderAddress(domain.getSenderAddress());
    entity.setSenderWardId(domain.getSenderWardId().value());
    entity.setSenderProvinceId(domain.getSenderProvinceId().value());

    entity.setRecipientName(domain.getRecipientName());
    entity.setRecipientPhone(domain.getRecipientPhone());
    entity.setRecipientAddress(domain.getRecipientAddress());
    entity.setRecipientWardId(domain.getRecipientWardId().value());
    entity.setRecipientProvinceId(domain.getRecipientProvinceId().value());

    entity.setDescription(domain.getDescription());
    entity.setWeight(domain.getWeight());
    entity.setValueDeclared(domain.getValueDeclared());
    entity.setFragile(domain.isFragile());
    entity.setRequiresSignature(domain.isRequiresSignature());
    entity.setShippingFee(domain.getShippingFee());
    entity.setInsuranceFee(domain.getInsuranceFee());
    entity.setTotalAmount(domain.getTotalAmount());

    entity.setAssignedDriverId(
        domain.getAssignedDriverId() != null ? domain.getAssignedDriverId().value() : null);
    entity.setDepotId(domain.getDepotId());
    entity.setEstimatedDeliveryDate(domain.getEstimatedDeliveryDate());
    entity.setActualDeliveryDate(domain.getActualDeliveryDate());
    entity.setNotes(domain.getNotes());
    entity.setRejectionReason(domain.getRejectionReason());

    try {
      entity.setDimensions(objectMapper.writeValueAsString(domain.getDimensions()));
    } catch (Exception e) {
      throw new AppException(
          CommonErrorCode.UNCATEGORIZED_EXCEPTION, "Failed to serialize Dimensions");
    }

    return entity;
  }

  public Order toDomain(OrderJpaEntity entity) {
    Dimensions dimensions;
    try {
      dimensions = objectMapper.readValue(entity.getDimensions(), Dimensions.class);
    } catch (Exception e) {
      throw new AppException(
          CommonErrorCode.UNCATEGORIZED_EXCEPTION, "Failed to deserialize Dimensions");
    }

    return Order.rehydrate(
        new OrderId(entity.getId()),
        entity.getTrackingCode(),
        entity.getType(),
        entity.getStatus(),
        new UserId(entity.getCustomerId()),
        new UserId(entity.getSenderId()),
        entity.getSenderName(),
        entity.getSenderPhone(),
        entity.getSenderAddress(),
        new WardId(entity.getSenderWardId()),
        new ProvinceId(entity.getSenderProvinceId()),
        entity.getRecipientName(),
        entity.getRecipientPhone(),
        entity.getRecipientAddress(),
        new WardId(entity.getRecipientWardId()),
        new ProvinceId(entity.getRecipientProvinceId()),
        entity.getDescription(),
        entity.getWeight(),
        dimensions,
        entity.getValueDeclared(),
        entity.isFragile(),
        entity.isRequiresSignature(),
        entity.getShippingFee(),
        entity.getInsuranceFee(),
        entity.getTotalAmount(),
        entity.getAssignedDriverId() != null ? new UserId(entity.getAssignedDriverId()) : null,
        entity.getDepotId(),
        entity.getEstimatedDeliveryDate(),
        entity.getActualDeliveryDate(),
        entity.getNotes(),
        entity.getRejectionReason());
  }

  public void updateEntityFromDomain(Order order, OrderJpaEntity entity) {
    entity.setTrackingCode(order.getTrackingCode());
    entity.setType(order.getType());
    entity.setStatus(order.getStatus());

    entity.setCustomerId(order.getCustomerId().value());
    entity.setSenderId(order.getSenderId().value());
    entity.setSenderName(order.getSenderName());
    entity.setSenderPhone(order.getSenderPhone());
    entity.setSenderAddress(order.getSenderAddress());
    entity.setSenderWardId(order.getSenderWardId().value());
    entity.setSenderProvinceId(order.getSenderProvinceId().value());

    entity.setRecipientName(order.getRecipientName());
    entity.setRecipientPhone(order.getRecipientPhone());
    entity.setRecipientAddress(order.getRecipientAddress());
    entity.setRecipientWardId(order.getRecipientWardId().value());
    entity.setRecipientProvinceId(order.getRecipientProvinceId().value());

    entity.setDescription(order.getDescription());
    entity.setWeight(order.getWeight());
    entity.setValueDeclared(order.getValueDeclared());
    entity.setFragile(order.isFragile());
    entity.setRequiresSignature(order.isRequiresSignature());
    entity.setShippingFee(order.getShippingFee());
    entity.setInsuranceFee(order.getInsuranceFee());
    entity.setTotalAmount(order.getTotalAmount());

    entity.setAssignedDriverId(
        order.getAssignedDriverId() != null ? order.getAssignedDriverId().value() : null);
    entity.setDepotId(order.getDepotId());
    entity.setEstimatedDeliveryDate(order.getEstimatedDeliveryDate());
    entity.setActualDeliveryDate(order.getActualDeliveryDate());
    entity.setNotes(order.getNotes());
    entity.setRejectionReason(order.getRejectionReason());

    try {
      entity.setDimensions(objectMapper.writeValueAsString(order.getDimensions()));
    } catch (Exception e) {
      throw new AppException(
          CommonErrorCode.UNCATEGORIZED_EXCEPTION, "Failed to serialize Dimensions");
    }
  }
}
