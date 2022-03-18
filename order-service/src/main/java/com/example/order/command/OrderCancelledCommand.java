package com.example.order.command;

import com.example.order.entity.OrderEntity;
import com.example.order.enums.OrderStatus;
import com.example.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderCancelledCommand extends CancelledCommand {

	private final OrderRepository orderRepository;

	public OrderCancelledCommand(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	public void execute(OrderEntity entity) {
		entity.setStatus(getConditionStatus());
	}

	@Override
	public OrderStatus getPreconditionStatus() {
		return OrderStatus.INVENTORY_CANCELLED;
	}

	@Override
	public OrderStatus getConditionStatus() {
		return OrderStatus.ORDER_CANCELLED;
	}

	@Override
	public OrderStatus getPostConditionStatus(Boolean success) {
		return OrderStatus.ORDER_CANCELLED;
	}
}
