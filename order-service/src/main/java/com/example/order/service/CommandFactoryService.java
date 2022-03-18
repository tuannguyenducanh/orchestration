package com.example.order.service;

import com.example.order.command.Command;
import com.example.order.enums.OrderStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class CommandFactoryService {

	private static final Map<OrderStatus, Command> commandsByPreEvent = new EnumMap<>(OrderStatus.class);
	private List<Command> commands;

	public CommandFactoryService(List<Command> commands) {
		this.commands = commands;
	}

	@PostConstruct
	public void initCommandCache() {
		for (Command command : commands) {
			commandsByPreEvent.put(command.getConditionStatus(), command);
		}
	}

	public Command getCommand(OrderStatus status) {
		return commandsByPreEvent.get(status);
	}

	public Command getNextCommand(OrderStatus status) {
		return commands.stream().filter(command -> command.getPreconditionStatus() == status).findFirst().orElse(null);
	}
}
