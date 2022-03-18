package com.example.order.command;

import com.example.order.entity.OrderEntity;
import com.example.order.enums.OrderStatus;
import com.example.order.event.InventoryEvent;
import com.example.order.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.order.enums.OrderStatus.INVENTORY_CANCELATION_PENDING;
import static com.example.order.enums.OrderStatus.INVENTORY_CANCELLED;
import static com.example.order.enums.OrderStatus.PAYMENT_CANCELLED;

@Slf4j
@Service
public class InventoryCancelledCommand extends CancelledCommand {

	private static final String INVENTORY_REQUEST_TOPIC = "inventory.request";

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private final OrderRepository orderRepository;

	public InventoryCancelledCommand(KafkaTemplate<String, String> kafkaTemplate,
									 ObjectMapper objectMapper,
									 OrderRepository orderRepository) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
		this.orderRepository = orderRepository;
	}

	@Override
	@Transactional
	public void execute(OrderEntity orderEntity) {
		InventoryEvent inventoryEvent = InventoryEvent.builder()
				.orderId(orderEntity.getId())
				.product(orderEntity.getProduct())
				.amount(orderEntity.getAmount())
				.eventType(getOrderEventType())
				.build();
		orderEntity.setStatus(getConditionStatus());
		orderRepository.save(orderEntity);
		log.info("Inventory request message: {}", inventoryEvent);
		try {
			kafkaTemplate.send(INVENTORY_REQUEST_TOPIC, objectMapper.writeValueAsString(inventoryEvent));
		} catch (JsonProcessingException e) {
			log.error("{} cannot parse to json", inventoryEvent);
		}
	}

	@Override
	public OrderStatus getPreconditionStatus() {
		return PAYMENT_CANCELLED;
	}

	@Override
	public OrderStatus getConditionStatus() {
		return INVENTORY_CANCELATION_PENDING;
	}

	@Override
	public OrderStatus getPostConditionStatus(Boolean success) {
		return INVENTORY_CANCELLED;
	}
}
