package com.gmapex.orderservice;

import com.gmapex.orderservice.application.service.OrderService;
import com.gmapex.orderservice.domain.model.Order;
import com.gmapex.orderservice.domain.repository.OrderRepository;
import com.gmapex.orderservice.infrastructure.messaging.KafkaOrderPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

	private OrderRepository repository;
	private KafkaOrderPublisher publisher;
	private OrderService service;

	@BeforeEach
	void setUp() {
		repository = mock(OrderRepository.class);
		publisher = mock(KafkaOrderPublisher.class);
		service = new OrderService(repository, publisher);
	}

	@Test
	void createOrder_ShouldInitializeFieldsAndSave() {
		Order order = new Order();
		order.setCustomerId("123");

		when(repository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

		Order result = service.createOrder(order);

		assertEquals("NEW", result.getStatus());
		assertNotNull(result.getCreatedAt());
		assertNotNull(result.getUpdatedAt());
		verify(repository).save(order);
	}

	@Test
	void getById_ShouldReturnOrderIfExists() {
		Order order = new Order();
		order.setId("1");
		when(repository.findById("1")).thenReturn(Optional.of(order));

		Order result = service.getById("1");

		assertEquals("1", result.getId());
		verify(repository).findById("1");
	}

	@Test
	void updateStatus_ShouldUpdateAndPublishEvent() {
		Order order = new Order();
		order.setId("1");
		order.setStatus("NEW");

		when(repository.findById("1")).thenReturn(Optional.of(order));
		when(repository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

		Order updated = service.updateStatus("1", "DELIVERED");

		assertEquals("DELIVERED", updated.getStatus());
		verify(repository).save(order);
		verify(publisher).publishStatusChange("1", "NEW", "DELIVERED");
	}

	@Test
	void updateStatus_ShouldThrowIfOrderNotFound() {
		when(repository.findById("404")).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> service.updateStatus("404", "DELIVERED"));
	}
}