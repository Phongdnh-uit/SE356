package com.uit.se356.delivery.presentation.messaging.consumers;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TestListener {

  @KafkaListener(id = "test-listener", topics = "test-topic", groupId = "test-group")
  public void test(Map<String, Object> message, Acknowledgment acknowledgment) {
    log.info("Processing message from test-topic: {}", message);
    acknowledgment.acknowledge();
  }
}
