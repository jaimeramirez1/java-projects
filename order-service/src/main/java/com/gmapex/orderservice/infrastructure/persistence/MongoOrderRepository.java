package com.gmapex.orderservice.infrastructure.persistence;

import com.gmapex.orderservice.domain.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoOrderRepository extends MongoRepository<Order, String> {
    List<Order> findByStatusAndCustomerId(String status, String customerId);
}