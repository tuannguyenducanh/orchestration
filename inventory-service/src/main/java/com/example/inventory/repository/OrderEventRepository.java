package com.example.inventory.repository;

import com.example.inventory.entity.OrderEventEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderEventRepository extends CrudRepository<OrderEventEntity, String> {

	List<OrderEventEntity> findByInventoryEventIdOrderIdOrderByDateTimeDesc(String orderId);
}
