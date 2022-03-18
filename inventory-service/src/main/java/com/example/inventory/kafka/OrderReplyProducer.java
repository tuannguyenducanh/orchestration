package com.example.inventory.kafka;

import com.example.inventory.model.OrderReplyEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderReplyProducer {

	public static final String ORDER_REPLY_TOPIC = "order.reply";

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public OrderReplyProducer(KafkaTemplate kafkaTemplate,
							  ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}

	public void sendMessage(OrderReplyEvent message) {
		log.info("Sending inventory processed message: " + message);
		try {
			kafkaTemplate.send(ORDER_REPLY_TOPIC, objectMapper.writeValueAsString(message));
			log.info("Inventory sent reply for order {} to topic {}", message.toString(), ORDER_REPLY_TOPIC);
		} catch (JsonProcessingException e) {
			log.error(message + " cannot parse to json");
		}
	}
}
