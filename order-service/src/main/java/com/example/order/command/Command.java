package com.example.order.command;

import com.example.order.enums.OrderEventType;
import com.example.order.enums.OrderStatus;

public interface Command<T> {

	void execute(T entity);
	OrderStatus getPreconditionStatus();
	OrderStatus getConditionStatus();
	OrderStatus getPostConditionStatus(Boolean success);
	OrderEventType getOrderEventType();

}
