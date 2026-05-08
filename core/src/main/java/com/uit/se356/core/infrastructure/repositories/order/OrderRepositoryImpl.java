package com.uit.se356.core.infrastructure.repositories.order;

import com.uit.se356.common.dto.PageResponse;
import com.uit.se356.common.dto.SearchPageable;
import com.uit.se356.common.utils.PageableUtil;
import com.uit.se356.core.application.order.port.OrderRepository;
import com.uit.se356.core.application.order.projections.OrderDetailProjection;
import com.uit.se356.core.application.order.projections.OrderSummaryProjection;
import com.uit.se356.core.domain.entities.order.Order;
import com.uit.se356.core.domain.vo.order.OrderId;
import com.uit.se356.core.infrastructure.persistence.entities.order.OrderJpaEntity;
import com.uit.se356.core.infrastructure.persistence.mappers.order.OrderPersistenceMapper;
import com.uit.se356.core.infrastructure.persistence.repositories.order.OrderJpaRepository;
import io.github.perplexhub.rsql.RSQLJPASupport;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements OrderRepository {

  private final OrderJpaRepository orderJpaRepository;
  private final OrderPersistenceMapper mapper;

  @Override
  public Order save(Order order) {
    OrderJpaEntity jpaEntity = mapper.toEntity(order);
    OrderJpaEntity savedEntity = orderJpaRepository.save(jpaEntity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public Order update(Order order) {
    OrderJpaEntity entity =
        orderJpaRepository
            .findById(order.getId().value())
            .orElseThrow(
                () -> new RuntimeException("Order not found with id: " + order.getId().value()));
    mapper.updateEntityFromDomain(order, entity);
    OrderJpaEntity updatedEntity = orderJpaRepository.save(entity);
    return mapper.toDomain(updatedEntity);
  }

  @Override
  public Optional<Order> findById(OrderId id) {
    return orderJpaRepository.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  public Optional<Order> findByTrackingCode(String trackingCode) {
    return orderJpaRepository.findByTrackingCode(trackingCode).map(mapper::toDomain);
  }

  @Override
  public Optional<OrderDetailProjection> findDetailById(OrderId id) {
    return orderJpaRepository.findDetailById(id.value());
  }

  @Override
  public PageResponse<OrderSummaryProjection> findAll(SearchPageable searchCriteria) {
    // 1. Chuyển đổi filter sang RSQL
    Specification<OrderJpaEntity> spec = RSQLJPASupport.toSpecification(searchCriteria.filter());
    // 2. Tạo pageable với sort
    Pageable pageable = PageableUtil.createPageable(searchCriteria);
    var page =
        orderJpaRepository.findBy(spec, q -> q.as(OrderSummaryProjection.class).page(pageable));
    return PageResponse.from(page);
  }

  @Override
  public boolean existsByTrackingCode(String trackingCode) {
    return orderJpaRepository.existsByTrackingCode(trackingCode);
  }

  @Override
  public void deleteById(OrderId id) {
    orderJpaRepository.deleteById(id.value());
  }
}
