package com.example.inventory.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_event")
public class OrderEventEntity {

	@EmbeddedId
	private InventoryEventId inventoryEventId;

	private String product;

	private Integer amount;

	private String reason;

	private LocalDateTime dateTime;

}