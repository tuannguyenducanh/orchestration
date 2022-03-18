package com.example.inventory.service;

import com.example.inventory.enums.OrderEventType;
import com.example.inventory.event.OrderRequestInventoryEvent;

public interface InventoryEventHandler {

	void handle(OrderRequestInventoryEvent orderRequestInventoryEvent);

	OrderEventType getStatus();
}
