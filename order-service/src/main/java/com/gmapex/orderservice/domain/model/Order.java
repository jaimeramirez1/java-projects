package com.gmapex.orderservice.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String customerId;
    private String status; // NEW, IN_PROGRESS, DELIVERED, CANCELLED
    private List<OrderItem> items;
    private Instant createdAt;
    private Instant updatedAt;
}