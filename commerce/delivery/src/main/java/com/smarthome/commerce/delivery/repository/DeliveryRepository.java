package com.smarthome.commerce.delivery.repository;

import java.util.Optional;
import java.util.UUID;

import com.smarthome.commerce.delivery.model.DeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<DeliveryEntity, UUID> {

    Optional<DeliveryEntity> findFirstByOrderIdOrderByDeliveryId(UUID orderId);
}
