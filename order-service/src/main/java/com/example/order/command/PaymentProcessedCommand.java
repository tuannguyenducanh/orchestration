package com.example.order.command;

import com.example.order.entity.OrderEntity;
import com.example.order.enums.OrderStatus;
import com.example.order.event.PaymentEvent;
import com.example.order.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.order.enums.OrderStatus.INVENTORY_APPROVED;
import static com.example.order.enums.OrderStatus.PAYMENT_APPROVED;
import static com.example.order.enums.OrderStatus.PAYMENT_CANCELLED;
import static com.example.order.enums.OrderStatus.PAYMENT_PENDING;

@Slf4j
@Service
public class PaymentProcessedCommand extends ProcessedCommand {

	private static final String TOPIC = "payment.request";

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;
	private final OrderRepository orderRepository;

	public PaymentProcessedCommand(KafkaTemplate<String, String> kafkaTemplate,
								   ObjectMapper objectMapper,
								   OrderRepository orderRepository) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
		this.orderRepository = orderRepository;
	}

	@Override
	@Transactional
	public void execute(OrderEntity orderEntity) {
		PaymentEvent paymentEvent = PaymentEvent.builder()
				.orderId(orderEntity.getId())
				.balance(orderEntity.getPrice() * orderEntity.getAmount())
				.username(orderEntity.getUsername())
				.eventType(getOrderEventType())
				.build();
		orderEntity.setStatus(getConditionStatus());
		orderRepository.save(orderEntity);
		log.info("Payment request message: {}", paymentEvent);
		try {
			kafkaTemplate.send(TOPIC, objectMapper.writeValueAsString(paymentEvent));
		} catch (JsonProcessingException e) {
			log.error("{} cannot parse to json", paymentEvent);
		}
	}

	@Override
	public OrderStatus getPreconditionStatus() {
		return INVENTORY_APPROVED;
	}

	@Override
	public OrderStatus getConditionStatus() {
		return PAYMENT_PENDING;
	}

	@Override
	public OrderStatus getPostConditionStatus(Boolean success) {
		if (success) {
			return PAYMENT_APPROVED;
		}
		return PAYMENT_CANCELLED;
	}

}
