package com.example.invoice.repository;

import com.example.invoice.entity.InvoiceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends CrudRepository<InvoiceEntity, String> {

	Optional<InvoiceEntity> findByOrderId(String orderId);
}
