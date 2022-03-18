package com.example.order.command;

import com.example.order.entity.OrderEntity;
import com.example.order.enums.OrderStatus;
import com.example.order.event.InvoiceEvent;
import com.example.order.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.order.enums.OrderStatus.INVENTORY_CANCELLED;
import static com.example.order.enums.OrderStatus.INVOICE_APPROVED;
import static com.example.order.enums.OrderStatus.INVOICE_PENDING;
import static com.example.order.enums.OrderStatus.PAYMENT_APPROVED;

@Slf4j
@Service
public class InvoiceProcessedCommand extends ProcessedCommand {

	private static final String INVOICE_REQUEST_TOPIC = "invoice.request";

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private final OrderRepository orderRepository;

	public InvoiceProcessedCommand(KafkaTemplate<String, String> kafkaTemplate,
								   ObjectMapper objectMapper,
								   OrderRepository orderRepository) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
		this.orderRepository = orderRepository;
	}

	@Override
	@Transactional
	public void execute(OrderEntity orderEntity) {
		InvoiceEvent inventoryRequestEvent = InvoiceEvent.builder()
				.orderId(orderEntity.getId())
				.product(orderEntity.getProduct())
				.amount(orderEntity.getAmount())
				.username(orderEntity.getUsername())
				.address(orderEntity.getAddress())
				.eventType(getOrderEventType())
				.build();
		orderEntity.setStatus(getConditionStatus());
		orderRepository.save(orderEntity);
		log.info("Invoice request message: {}", inventoryRequestEvent);
		try {
			kafkaTemplate.send(INVOICE_REQUEST_TOPIC, objectMapper.writeValueAsString(inventoryRequestEvent));
		} catch (JsonProcessingException e) {
			log.error("{} cannot parse to json", inventoryRequestEvent);
		}
	}

	@Override
	public OrderStatus getPreconditionStatus() {
		return PAYMENT_APPROVED;
	}

	@Override
	public OrderStatus getConditionStatus() {
		return INVOICE_PENDING;
	}

	@Override
	public OrderStatus getPostConditionStatus(Boolean success) {
		if (success) {
			return INVOICE_APPROVED;
		}
		return INVENTORY_CANCELLED;
	}

}
