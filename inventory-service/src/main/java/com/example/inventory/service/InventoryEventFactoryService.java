package com.example.inventory.service;

import com.example.inventory.enums.OrderEventType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class InventoryEventFactoryService {

	private static final Map<OrderEventType, InventoryEventHandler> handlerByEvent = new EnumMap<>(OrderEventType.class);

	private List<InventoryEventHandler> inventoryEventHandlers;

	public InventoryEventFactoryService(List<InventoryEventHandler> inventoryEventHandlers) {
		this.inventoryEventHandlers = inventoryEventHandlers;
	}

	@PostConstruct
	public void initHandlerByEvents() {
		for(InventoryEventHandler inventoryEventHandler : inventoryEventHandlers) {
			handlerByEvent.put(inventoryEventHandler.getStatus(), inventoryEventHandler);
		}
	}

	public InventoryEventHandler getHandler(OrderEventType status) {
		return handlerByEvent.get(status);
	}
}
