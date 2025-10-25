package com.gmapex.orderservice.domain.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    private String sku;
    private int quantity;
    private double price;
}