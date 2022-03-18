package com.example.payment.entity;

import com.example.payment.enums.PaymentStatus;
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

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment")
public class PaymentEntity {

	@Id
	@Column(name = "ID")
	private String id;

	@Column(name = "ORDER_ID")
	private String orderId;

	@Column(name = "TOTAL")
	private Integer total;

	@Column(name = "USERNAME")
	private String username;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private PaymentStatus status;

	public boolean isCancelled() {
		return status != PaymentStatus.FAILED && status != PaymentStatus.CANCELLED;
	}
}
