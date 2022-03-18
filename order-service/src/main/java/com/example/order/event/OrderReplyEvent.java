package com.example.order.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderReplyEvent {

	private String orderId;
	private Boolean success;
	private String exception;

}
