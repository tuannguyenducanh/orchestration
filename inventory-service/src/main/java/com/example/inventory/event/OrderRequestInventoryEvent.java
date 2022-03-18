package com.example.inventory.event;

import com.example.inventory.enums.OrderEventType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderRequestInventoryEvent {

	private String orderId;
	private String product;
	private Integer amount;
	private OrderEventType eventType;

}