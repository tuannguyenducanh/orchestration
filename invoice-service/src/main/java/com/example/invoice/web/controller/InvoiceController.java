package com.example.invoice.web.controller;

import com.example.invoice.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class InvoiceController {

	private final InvoiceService invoiceService;

	public InvoiceController(InvoiceService invoiceService) {
		this.invoiceService = invoiceService;
	}

	@GetMapping("/{orderId}")
	public ResponseEntity getInvoiceByOrderId(@PathVariable String orderId) {
		return ResponseEntity.ok(invoiceService.findInvoiceByOrderId(orderId));
	}
}
