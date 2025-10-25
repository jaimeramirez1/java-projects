package com.gmapex.orderservice.application.service;

import com.gmapex.orderservice.domain.model.Order;
import com.gmapex.orderservice.domain.repository.OrderRepository;
import com.gmapex.orderservice.infrastructure.messaging.KafkaOrderPublisher;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final KafkaOrderPublisher publisher;

    public OrderService(OrderRepository repository, KafkaOrderPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public Order createOrder(Order order) {
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(Instant.now());
        order.setStatus("NEW");
        return repository.save(order);
    }

    @Cacheable(value = "orders", key = "#id", unless = "#result == null", cacheManager = "redisCacheManager")
    public Order getById(String id) {
        return repository.findById(id).orElse(null);
    }

    public List<Order> findByStatusAndCustomerId(String status, String customerId) {
        return repository.findByStatusAndCustomerId(status, customerId);
    }

    @CacheEvict(value = "orders", key = "#id", cacheManager = "redisCacheManager")
    public Order updateStatus(String id, String newStatus) {
        var orderOpt = repository.findById(id);
        if (orderOpt.isEmpty()) throw new IllegalArgumentException("Order not found");
        Order order = orderOpt.get();
        String oldStatus = order.getStatus();
        order.setStatus(newStatus);
        order.setUpdatedAt(Instant.now());
        Order saved = repository.save(order);

        // publish event
        publisher.publishStatusChange(saved.getId(), oldStatus, newStatus);
        return saved;
    }
}