package com.gmapex.orderservice.infrastructure.persistence;

import com.gmapex.orderservice.domain.model.Order;
import com.gmapex.orderservice.domain.repository.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MongoOrderRepositoryAdapter implements OrderRepository {

    private final MongoOrderRepository repo;

    public MongoOrderRepositoryAdapter(MongoOrderRepository repo) {
        this.repo = repo;
    }

    @Override
    public Order save(Order order) {
        return repo.save(order);
    }

    @Override
    public Optional<Order> findById(String id) {
        return repo.findById(id);
    }

    @Override
    public List<Order> findByStatusAndCustomerId(String status, String customerId) {
        return repo.findByStatusAndCustomerId(status, customerId);
    }
}