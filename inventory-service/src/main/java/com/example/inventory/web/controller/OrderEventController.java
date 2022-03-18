package com.example.inventory.web.controller;

import com.example.inventory.service.OrderEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/order-event")
public class OrderEventController {

	private final OrderEventService orderEventService;

	public OrderEventController(OrderEventService orderEventService) {
		this.orderEventService = orderEventService;
	}

	@GetMapping
	public ResponseEntity findOrderEventById(@RequestParam String orderId) {
		log.info("getPaymentByOrderId {}", orderId);
		return ResponseEntity.ok(orderEventService.findOrderEventById(orderId));
	}

}
