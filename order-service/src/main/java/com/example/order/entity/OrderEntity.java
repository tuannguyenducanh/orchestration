package com.example.order.entity;

import com.example.order.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import static com.example.order.enums.OrderStatus.ORDER_APPROVED;
import static com.example.order.enums.OrderStatus.ORDER_CANCELLED;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "\"order\"")
public class OrderEntity {

	@Id
	private String id;

	@Column(name = "product")
	private String product;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	private String username;

	private Integer amount;

	private Integer price;

	private String reason;

	private String address;

	public boolean isCancelled() {
		return status != ORDER_APPROVED && status != ORDER_CANCELLED;
	}

}
