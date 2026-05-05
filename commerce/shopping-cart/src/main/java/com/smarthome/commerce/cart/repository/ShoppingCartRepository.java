package com.smarthome.commerce.cart.repository;

import java.util.Optional;
import java.util.UUID;

import com.smarthome.commerce.cart.model.ShoppingCartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCartEntity, UUID> {

    Optional<ShoppingCartEntity> findByUsername(String username);
}
