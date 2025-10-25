package com.gmapex.orderservice.infrastructure.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class KafkaOrderPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaOrderPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishStatusChange(String orderId, String oldStatus, String newStatus) {
        Map<String, Object> event = Map.of(
                "orderId", orderId,
                "oldStatus", oldStatus,
                "newStatus", newStatus,
                "timestamp", Instant.now().toString()
        );
        kafkaTemplate.send("orders.events", orderId, event);
    }
}