package com.gmapex.orderservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmapex.orderservice.application.service.OrderService;
import com.gmapex.orderservice.domain.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setCustomerId("CUST-001");
        order.setStatus("NEW");
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(Instant.now());
    }

    @Test
    void testCreateOrder() throws Exception {
        Mockito.when(orderService.createOrder(Mockito.any(Order.class)))
                .thenReturn(order);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    void testGetOrderById() throws Exception {
        Mockito.when(orderService.getById(order.getId()))
                .thenReturn(order);

        mockMvc.perform(get("/orders/{id}", order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("CUST-001"));
    }

    @Test
    void testFindByStatusAndCustomerId() throws Exception {
        Mockito.when(orderService.findByStatusAndCustomerId("NEW", "CUST-001"))
                .thenReturn(Collections.singletonList(order));

        mockMvc.perform(get("/orders")
                        .param("status", "NEW")
                        .param("customerId", "CUST-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("NEW"));
    }

    @Test
    void testUpdateStatus() throws Exception {
        Mockito.when(orderService.updateStatus(order.getId(), "COMPLETED"))
                .thenReturn(order);

        mockMvc.perform(patch("/orders/{id}/status", order.getId())
                        .param("newStatus", "COMPLETED"))
                .andExpect(status().isOk());
    }
}