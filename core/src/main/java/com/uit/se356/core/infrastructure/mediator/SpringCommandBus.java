package com.uit.se356.core.infrastructure.mediator;

import com.uit.se356.common.dto.Command;
import com.uit.se356.common.exception.AppException;
import com.uit.se356.common.exception.CommonErrorCode;
import com.uit.se356.common.services.CommandBus;
import com.uit.se356.common.services.CommandHandler;
import com.uit.se356.core.application.authentication.command.RegisterCommand;
import com.uit.se356.core.application.authentication.result.RegisterResult;
import com.uit.se356.core.domain.events.UserCreatedEvent;
import com.uit.se356.core.domain.vo.authentication.UserId;
import com.uit.se356.core.infrastructure.middleware.MetricTracker;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpringCommandBus implements CommandBus {
  private final Map<Class<? extends Command<?>>, CommandHandler<?, ?>> handlers;
  private final MetricTracker metricTracker;
  private final ApplicationEventPublisher eventPublisher;

  @SuppressWarnings("unchecked")
  public SpringCommandBus(
      List<CommandHandler<?, ?>> commandHandlers,
      MetricTracker metricTracker,
      ApplicationEventPublisher eventPublisher) {
    this.handlers = new HashMap<>();
    commandHandlers.forEach(
        handler -> {
          Class<?> targetClass = AopUtils.getTargetClass(handler);
          ResolvableType resolvableType =
              ResolvableType.forClass(targetClass).as(CommandHandler.class);
          Class<?> commandType = resolvableType.getGeneric(0).resolve();
          if (commandType != null && Command.class.isAssignableFrom(commandType)) {
            handlers.put((Class<? extends Command<?>>) commandType, handler);
          }
        });
    this.metricTracker = metricTracker;
    this.eventPublisher = eventPublisher;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <R> R dispatch(Command<R> command) {
    CommandHandler<?, ?> handler = handlers.get(command.getClass());
    if (handler == null) {
      log.error("No handler found for command: {}", command.getClass().getName());
      throw new AppException(CommonErrorCode.INTERNAL_ERROR);
    }
    try {
      R result =
          (R)
              metricTracker.observe(
                  "command",
                  command.getClass().getSimpleName(),
                  () -> ((CommandHandler<Command<R>, R>) handler).handle(command));

      // Hướng tiếp cận Event-driven tại tầng Infrastructure
      postProcess(command, result);

      return result;
    } catch (ClassCastException e) {
      log.error("Handler type mismatch for command: {}", command.getClass().getName(), e);
      throw new AppException(CommonErrorCode.INTERNAL_ERROR);
    }
  }

  private void postProcess(Command<?> command, Object result) {
    // Nếu là RegisterCommand thành công, bắn UserCreatedEvent
    if (command instanceof RegisterCommand && result instanceof RegisterResult registerResult) {
      log.info(
          "RegisterCommand successful for user {}, publishing UserCreatedEvent",
          registerResult.id());
      eventPublisher.publishEvent(new UserCreatedEvent(new UserId(registerResult.id())));
    }
  }
}
