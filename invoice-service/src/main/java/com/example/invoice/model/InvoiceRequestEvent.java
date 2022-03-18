package com.example.invoice.model;

import com.example.invoice.enums.OrderEventType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InvoiceRequestEvent {

	private String orderId;
	private String product;
	private Integer amount;
	private String username;
	private String address;
	private OrderEventType eventType;

}
