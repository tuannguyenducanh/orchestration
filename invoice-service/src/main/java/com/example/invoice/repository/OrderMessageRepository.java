package com.example.invoice.repository;

import com.example.invoice.entity.OrderMessageEntity;
import org.springframework.data.repository.CrudRepository;

public interface OrderMessageRepository extends CrudRepository<OrderMessageEntity, String> {
}
