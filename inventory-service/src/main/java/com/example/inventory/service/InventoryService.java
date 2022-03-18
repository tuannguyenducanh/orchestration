package com.example.inventory.service;

import com.example.inventory.entity.InventoryEntity;
import com.example.inventory.event.OrderRequestInventoryEvent;
import com.example.inventory.kafka.OrderReplyProducer;
import com.example.inventory.model.OrderReplyEvent;
import com.example.inventory.repository.OrderEventRepository;
import com.example.inventory.repository.InventoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class InventoryService {

	private static final String INVENTORY_REQUEST_TOPIC = "inventory.request";
	private static final String ORDER_CANCEL_TOPIC = "order.cancel";

	private final ObjectMapper objectMapper;
	private final InventoryRepository inventoryRepository;
	private final OrderEventRepository orderEventRepository;
	private final OrderReplyProducer orderReplyProducer;
	private final InventoryEventFactoryService inventoryEventFactoryService;

	public InventoryService(InventoryRepository inventoryRepository,
							OrderEventRepository orderEventRepository,
						    ObjectMapper objectMapper,
							OrderReplyProducer orderReplyProducer,
							InventoryEventFactoryService inventoryEventFactoryService) {
		this.objectMapper = objectMapper;
		this.inventoryRepository = inventoryRepository;
		this.orderReplyProducer = orderReplyProducer;
		this.orderEventRepository = orderEventRepository;
		this.inventoryEventFactoryService = inventoryEventFactoryService;
	}

	@KafkaListener(topics = INVENTORY_REQUEST_TOPIC)
	public void comsumeOrderCreated(String message) throws JsonProcessingException {
		log.info("Consumer message {} from topic {}", message, INVENTORY_REQUEST_TOPIC);
		OrderRequestInventoryEvent orderRequestInventoryEvent = objectMapper.readValue(message, OrderRequestInventoryEvent.class);
		if (inventoryRepository.existsByProduct(orderRequestInventoryEvent.getProduct())) {
			inventoryEventFactoryService.getHandler(orderRequestInventoryEvent.getEventType()).handle(orderRequestInventoryEvent);
		} else {
			log.info("Could not finds product {} in message {}", orderRequestInventoryEvent.getProduct(), message);
			OrderReplyEvent orderExceptionReply = OrderReplyEvent.builder()
					.orderId(orderRequestInventoryEvent.getOrderId())
					.exception("Product not found")
					.success(Boolean.FALSE)
					.build();
			orderReplyProducer.sendMessage(orderExceptionReply);
		}
	}

	public InventoryEntity findByProduct(String product) {
		Optional<InventoryEntity> inventoryEntityOptional = inventoryRepository.findByProduct(product);
		if (inventoryEntityOptional.isPresent()) {
			return inventoryEntityOptional.get();
		}
		return null;
	}

//	@KafkaListener(topics = ORDER_CANCEL_TOPIC)
//	public void consumeOrderCancel(String message) throws JsonProcessingException {
//		OrderCancelEvent orderCancelEvent = objectMapper.readValue(message, OrderCancelEvent.class);
//		inventoryEventRepository.findByOrderId(orderCancelEvent.getOrderId()).ifPresentOrElse(inventoryEventEntity -> {
//			if (inventoryEventEntity.getStatus() == PROCESSED) {
//				inventoryRepository.findByProduct(inventoryEventEntity.getProduct()).ifPresentOrElse(inventoryEntity -> {
//					inventoryEventEntity.setStatus(REJECTED);
//					inventoryEventRepository.save(inventoryEventEntity);
//					inventoryEntity.addAmount(inventoryEventEntity.getAmount());
//					inventoryRepository.save(inventoryEntity);
//				}, () -> {
//					log.info("Product {} not exist", inventoryEventEntity.getProduct());
//				});
//			}
//		}, () -> {
//			log.info("Order id {} not exist", orderCancelEvent.getOrderId());
//		});
//	}

}