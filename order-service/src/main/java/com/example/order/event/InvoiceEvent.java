package com.example.order.event;

import com.example.order.enums.OrderEventType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceEvent {

	private String orderId;
	private String product;
	private Integer amount;
	private String username;
	private String address;
	private OrderEventType eventType;

}