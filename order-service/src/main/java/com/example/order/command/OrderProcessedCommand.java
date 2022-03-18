package com.example.order.command;

import com.example.order.entity.OrderEntity;
import com.example.order.enums.OrderStatus;
import com.example.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessedCommand extends ProcessedCommand {

	private final OrderRepository orderRepository;

	public OrderProcessedCommand(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Override
	public void execute(OrderEntity entity) {
		entity.setStatus(getConditionStatus());
		orderRepository.save(entity);
	}

	@Override
	public OrderStatus getPreconditionStatus() {
		return OrderStatus.INVOICE_APPROVED;
	}

	@Override
	public OrderStatus getConditionStatus() {
		return OrderStatus.ORDER_APPROVED;
	}

	@Override
	public OrderStatus getPostConditionStatus(Boolean success) {
		return OrderStatus.ORDER_APPROVED;
	}

}
