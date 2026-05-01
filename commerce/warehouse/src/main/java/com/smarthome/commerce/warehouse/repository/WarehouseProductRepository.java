package com.smarthome.commerce.warehouse.repository;

import java.util.UUID;

import com.smarthome.commerce.warehouse.model.WarehouseProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseProductRepository extends JpaRepository<WarehouseProductEntity, UUID> {
}
