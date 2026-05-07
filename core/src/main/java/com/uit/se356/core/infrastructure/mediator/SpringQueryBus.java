package com.uit.se356.core.infrastructure.mediator;

import com.uit.se356.common.dto.Query;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.common.services.QueryBus;
import com.uit.se356.common.services.QueryHandler;
import com.uit.se356.core.infrastructure.middleware.MetricTracker;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpringQueryBus implements QueryBus {
  private final Map<Class<? extends Query<?>>, QueryHandler<?, ?>> handlers;
  private final MetricTracker metricTracker;

  @SuppressWarnings("unchecked")
  public SpringQueryBus(List<QueryHandler<?, ?>> queryHandlers, MetricTracker metricTracker) {
    this.handlers = new HashMap<>();
    queryHandlers.forEach(
        handler -> {
          Class<?> targetClass = AopUtils.getTargetClass(handler);
          ResolvableType resolvableType =
              ResolvableType.forClass(targetClass).as(QueryHandler.class);
          Class<?> queryType = resolvableType.getGeneric(0).resolve();
          if (queryType != null && Query.class.isAssignableFrom(queryType)) {
            handlers.put((Class<? extends Query<?>>) queryType, handler);
          }
        });
    this.metricTracker = metricTracker;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <R> R dispatch(Query<R> query) {
    QueryHandler<?, ?> handler = handlers.get(query.getClass());
    if (handler == null) {
      log.error("No handler found for query: {}", query.getClass().getName());
      throw new AppException(CommonErrorCode.INTERNAL_ERROR);
    }
    try {
      return (R)
          metricTracker.observe(
              "query",
              query.getClass().getSimpleName(),
              () -> ((QueryHandler<Query<R>, R>) handler).handle(query));
    } catch (ClassCastException e) {
      log.error("Handler type mismatch for query: {}", query.getClass().getName(), e);
      throw new AppException(CommonErrorCode.INTERNAL_ERROR);
    }
  }
}
