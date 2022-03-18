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

import static com.example.order.enums.OrderStatus.INVOICE_CANCELATION_PENDING;
import static com.example.order.enums.OrderStatus.INVOICE_CANCELLED;
import static com.example.order.enums.OrderStatus.ORDER_CANCELLED;

@Slf4j
@Service
public class InvoiceCancelledCommand extends CancelledCommand {

	private static final String INVOICE_REQUEST_TOPIC = "invoice.request";

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private final OrderRepository orderRepository;

	public InvoiceCancelledCommand(KafkaTemplate<String, String> kafkaTemplate,
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
		return ORDER_CANCELLED;
	}

	@Override
	public OrderStatus getConditionStatus() {
		return INVOICE_CANCELATION_PENDING;
	}

	@Override
	public OrderStatus getPostConditionStatus(Boolean success) {
		return INVOICE_CANCELLED;
	}


}
