package com.example.inventory.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderCancelEvent {

	private String orderId;

}
