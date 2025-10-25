package com.gmapex.orderservice.domain.repository;

import com.gmapex.orderservice.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(String id);
    List<Order> findByStatusAndCustomerId(String status, String customerId);
}