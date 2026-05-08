package com.smarthome.commerce.order.repository;

import java.util.List;
import java.util.UUID;

import com.smarthome.commerce.order.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    List<OrderEntity> findByUsername(String username);
}
