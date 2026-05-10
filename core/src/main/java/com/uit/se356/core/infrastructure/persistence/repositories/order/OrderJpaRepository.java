package com.uit.se356.core.infrastructure.persistence.repositories.order;

import com.uit.se356.common.repository.CommonRepository;
import com.uit.se356.core.application.order.projections.OrderDetailProjection;
import com.uit.se356.core.infrastructure.persistence.entities.order.OrderJpaEntity;
import java.util.Optional;

public interface OrderJpaRepository extends CommonRepository<OrderJpaEntity, String> {
  Optional<OrderJpaEntity> findByTrackingCode(String trackingCode);

  boolean existsByTrackingCode(String trackingCode);

  Optional<OrderDetailProjection> findDetailById(String id);
}
