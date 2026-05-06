package com.smarthome.commerce.warehouse.repository;

import java.util.UUID;

import com.smarthome.commerce.warehouse.model.OrderBookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderBookingRepository extends JpaRepository<OrderBookingEntity, UUID> {
}
