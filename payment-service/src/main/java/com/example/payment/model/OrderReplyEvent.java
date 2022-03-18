package com.example.payment.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderReplyEvent {

	private String orderId;
	private Boolean success;
	private String exception;

}