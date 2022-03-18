package com.example.order.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderCancelEvent {

	private String orderId;

}
