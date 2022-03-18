package com.example.invoice.service;

import com.example.invoice.entity.InvoiceEntity;
import com.example.invoice.enums.InvoiceStatus;
import com.example.invoice.enums.OrderEventType;
import com.example.invoice.kafka.OrderReplyProducer;
import com.example.invoice.model.InvoiceRequestEvent;
import com.example.invoice.model.OrderReplyEvent;
import com.example.invoice.repository.InvoiceRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class InvoiceService {

	private static final String INVOICE_REQUEST_TOPIC = "invoice.request";

	private final ObjectMapper objectMapper;
	private final InvoiceRepository invoiceRepository;
	private final OrderReplyProducer orderReplyProducer;

	public InvoiceService(ObjectMapper objectMapper,
						  InvoiceRepository invoiceRepository,
						  OrderReplyProducer orderReplyProducer) {
		this.objectMapper = objectMapper;
		this.invoiceRepository = invoiceRepository;
		this.orderReplyProducer = orderReplyProducer;
	}

	@KafkaListener(topics = INVOICE_REQUEST_TOPIC)
	public void consumeInvoiceRequest(String message) throws JsonProcessingException {
		log.info("Consumer message {} from topic {}", message, INVOICE_REQUEST_TOPIC);
		InvoiceRequestEvent invoiceRequestEvent = objectMapper.readValue(message, InvoiceRequestEvent.class);
		invoiceRepository.findByOrderId(invoiceRequestEvent.getOrderId())
				.ifPresentOrElse(entity -> {
					log.info("Cannot handle event {} for invoice entity {}", invoiceRequestEvent, entity);
					OrderReplyEvent event = OrderReplyEvent.builder()
							.orderId(invoiceRequestEvent.getOrderId())
							.success(Boolean.FALSE)
							.build();
					orderReplyProducer.sendMessage(event);
				}, () -> {
					log.info("Handle invoice request {} for non-exist invoice entity", invoiceRequestEvent);
					InvoiceEntity invoiceEntity = InvoiceEntity.builder()
							.id(UUID.randomUUID().toString())
							.product(invoiceRequestEvent.getProduct())
							.amount(invoiceRequestEvent.getAmount())
							.username(invoiceRequestEvent.getUsername())
							.address(invoiceRequestEvent.getAddress())
							.orderId(invoiceRequestEvent.getOrderId())
							.build();
					if (invoiceRequestEvent.getEventType() == OrderEventType.PROCESSED) {
						invoiceEntity.setStatus(InvoiceStatus.APPROVED);
					} else {
						invoiceEntity.setStatus(InvoiceStatus.REJECTED);
					}
					invoiceRepository.save(invoiceEntity);
					OrderReplyEvent event = OrderReplyEvent.builder()
							.orderId(invoiceRequestEvent.getOrderId())
							.success(Boolean.TRUE)
							.build();
					orderReplyProducer.sendMessage(event);
				});
	}

	public InvoiceEntity findInvoiceByOrderId(String orderId) {
		Optional<InvoiceEntity> invoiceEntityOptional = invoiceRepository.findByOrderId(orderId);
		if (invoiceEntityOptional.isPresent()) {
			return invoiceEntityOptional.get();
		}
		return null;
	}
}
