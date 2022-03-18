package com.example.inventory.entity;

import com.example.inventory.enums.OrderEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryEventId implements Serializable {

	private String orderId;

	@Enumerated(EnumType.STRING)
	private OrderEventType status;
}
