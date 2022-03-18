package com.example.invoice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_message")
public class OrderMessageEntity {

	@Id
	@Column(name = "ID")
	private String id;

	@Column(name = "ORDER_ID")
	private String orderId;

	@Column(name = "PRODUCT")
	private String product;

	@Column(name = "AMOUNT")
	private int amount;

	@Column(name = "PRICE")
	private int price;

	@Column(name = "TOTAL")
	private Integer total;

	@Column(name = "USERNAME")
	private String username;

	@Column(name = "ADDRESS")
	private String address;

}
