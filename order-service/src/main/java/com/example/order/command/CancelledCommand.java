package com.example.order.command;

import com.example.order.entity.OrderEntity;
import com.example.order.enums.OrderEventType;

public abstract class CancelledCommand implements Command<OrderEntity>{

	public OrderEventType getOrderEventType() {
		return OrderEventType.CANCELLED;
	}
}
