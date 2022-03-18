package com.example.inventory.service;

import com.example.inventory.entity.OrderEventEntity;
import com.example.inventory.entity.InventoryEventId;
import com.example.inventory.enums.OrderEventType;
import com.example.inventory.event.OrderRequestInventoryEvent;
import com.example.inventory.kafka.OrderReplyProducer;
import com.example.inventory.model.OrderReplyEvent;
import com.example.inventory.repository.InventoryRepository;
import com.example.inventory.repository.OrderEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.inventory.enums.OrderEventType.PROCESSED;
import static com.example.inventory.enums.OrderEventType.CANCELLED;

@Slf4j
@Service
public class ProcessedInventoryEventHandler implements  InventoryEventHandler {

	private final InventoryRepository inventoryRepository;
	private final OrderEventRepository orderEventRepository;
	private final OrderReplyProducer orderReplyProducer;

	public ProcessedInventoryEventHandler(InventoryRepository inventoryRepository,
							OrderEventRepository orderEventRepository,
							OrderReplyProducer orderReplyProducer) {
		this.inventoryRepository = inventoryRepository;
		this.orderReplyProducer = orderReplyProducer;
		this.orderEventRepository = orderEventRepository;
	}

	@Override
	@Transactional
	public void handle(OrderRequestInventoryEvent orderRequestInventoryEvent) {
		List<OrderEventEntity> orderEventEntities = orderEventRepository.findByInventoryEventIdOrderIdOrderByDateTimeDesc(orderRequestInventoryEvent.getOrderId());

		if (orderEventRepository.findByInventoryEventIdOrderIdOrderByDateTimeDesc(orderRequestInventoryEvent.getOrderId()).size() > 0) {
			return;
		}
		inventoryRepository.findByProduct(orderRequestInventoryEvent.getProduct()).ifPresent(inventoryEntity -> {
			if (inventoryEntity.getAmount() > orderRequestInventoryEvent.getAmount()) {
				inventoryEntity.minusAmount(orderRequestInventoryEvent.getAmount());
				inventoryRepository.save(inventoryEntity);
				OrderEventEntity orderEventEntity = OrderEventEntity.builder()
						.inventoryEventId(
								InventoryEventId.builder()
										.orderId(orderRequestInventoryEvent.getOrderId())
										.status(PROCESSED)
										.build())
						.amount(orderRequestInventoryEvent.getAmount())
						.product(orderRequestInventoryEvent.getProduct())
						.dateTime(LocalDateTime.now())
						.build();
				orderEventRepository.save(orderEventEntity);
				OrderReplyEvent orderReplyEvent = OrderReplyEvent.builder()
						.orderId(orderRequestInventoryEvent.getOrderId())
						.success(Boolean.TRUE)
						.build();
				orderReplyProducer.sendMessage(orderReplyEvent);
			} else {
				log.info("Required {} from product {}, only have {}", orderRequestInventoryEvent.getAmount(), orderRequestInventoryEvent.getProduct(), inventoryEntity.getAmount());
				OrderReplyEvent orderExceptionReply = OrderReplyEvent.builder()
						.orderId(orderRequestInventoryEvent.getOrderId())
						.success(Boolean.FALSE)
						.exception("Product does not have enough")
						.build();
				OrderEventEntity orderEventEntity = OrderEventEntity.builder()
						.inventoryEventId(
								InventoryEventId.builder()
										.orderId(orderRequestInventoryEvent.getOrderId())
										.status(CANCELLED)
										.build())
						.amount(orderRequestInventoryEvent.getAmount())
						.product(orderRequestInventoryEvent.getProduct())
						.reason("Product does not have enough")
						.build();
				orderEventRepository.save(orderEventEntity);
				orderReplyProducer.sendMessage(orderExceptionReply);
			}
		});
	}

	@Override
	public OrderEventType getStatus() {
		return OrderEventType.PROCESSED;
	}
}
