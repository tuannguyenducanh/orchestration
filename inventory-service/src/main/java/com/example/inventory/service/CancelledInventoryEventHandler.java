package com.example.inventory.service;

import com.example.inventory.entity.InventoryEventId;
import com.example.inventory.entity.OrderEventEntity;
import com.example.inventory.enums.OrderEventType;
import com.example.inventory.event.OrderRequestInventoryEvent;
import com.example.inventory.kafka.OrderReplyProducer;
import com.example.inventory.model.OrderReplyEvent;
import com.example.inventory.repository.InventoryRepository;
import com.example.inventory.repository.OrderEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.inventory.enums.OrderEventType.CANCELLED;

@Slf4j
@Service
public class CancelledInventoryEventHandler implements InventoryEventHandler {

	private final ObjectMapper objectMapper;
	private final InventoryRepository inventoryRepository;
	private final OrderEventRepository orderEventRepository;
	private final OrderReplyProducer orderReplyProducer;

	public CancelledInventoryEventHandler(InventoryRepository inventoryRepository,
										  OrderEventRepository orderEventRepository,
										  ObjectMapper objectMapper,
										  OrderReplyProducer orderReplyProducer) {
		this.objectMapper = objectMapper;
		this.inventoryRepository = inventoryRepository;
		this.orderReplyProducer = orderReplyProducer;
		this.orderEventRepository = orderEventRepository;
	}

	@Override
	@Transactional
	public void handle(OrderRequestInventoryEvent orderRequestInventoryEvent) {
		log.info("Handle cancel for event {}", orderRequestInventoryEvent);
		List<OrderEventEntity> orderEventEntities = orderEventRepository.findByInventoryEventIdOrderIdOrderByDateTimeDesc(orderRequestInventoryEvent.getOrderId());
		if (orderEventEntities.stream()
				.anyMatch(orderEventEntity -> orderEventEntity.getInventoryEventId().getStatus() == OrderEventType.CANCELLED)) {
			return;
		}

		OrderEventEntity orderEventEntity = OrderEventEntity.builder()
				.inventoryEventId(
						InventoryEventId.builder()
								.orderId(orderRequestInventoryEvent.getOrderId())
								.status(CANCELLED)
								.build())
				.amount(orderRequestInventoryEvent.getAmount())
				.product(orderRequestInventoryEvent.getProduct())
				.dateTime(LocalDateTime.now())
				.build();
		orderEventRepository.save(orderEventEntity);

		if (orderEventEntities.stream()
				.anyMatch(entity -> entity.getInventoryEventId().getStatus() == OrderEventType.PROCESSED)) {
			inventoryRepository.findByProduct(orderRequestInventoryEvent.getProduct()).ifPresent(entity -> {
				entity.addAmount(orderRequestInventoryEvent.getAmount());
				inventoryRepository.save(entity);
				log.info("Reply cancel cancel for entity {}", entity);
				OrderReplyEvent orderReplyEvent = OrderReplyEvent.builder()
						.orderId(orderRequestInventoryEvent.getOrderId())
						.success(Boolean.TRUE)
						.build();
				orderReplyProducer.sendMessage(orderReplyEvent);
			});
		}
	}

	@Override
	public OrderEventType getStatus() {
		return OrderEventType.CANCELLED;
	}
}
