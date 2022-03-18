package com.example.inventory.service;

import com.example.inventory.entity.OrderEventEntity;
import com.example.inventory.repository.OrderEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OrderEventService {

	private final OrderEventRepository orderEventRepository;

	public OrderEventService(OrderEventRepository orderEventRepository) {
		this.orderEventRepository = orderEventRepository;
	}

	public List<OrderEventEntity> findOrderEventById(String orderId) {
		return orderEventRepository.findByInventoryEventIdOrderIdOrderByDateTimeDesc(orderId);
	}
}
