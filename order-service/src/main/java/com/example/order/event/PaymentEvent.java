package com.example.order.event;

import com.example.order.enums.OrderEventType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentEvent {

	private String orderId;
	private String username;
	private Integer balance;
	private OrderEventType eventType;

}