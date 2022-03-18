package com.example.inventory.repository;

import com.example.inventory.entity.InventoryEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends CrudRepository<InventoryEntity, Long> {

	Optional<InventoryEntity> findByProduct(String product);
	boolean existsByProduct(String product);

}
