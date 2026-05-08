package com.smarthome.commerce.payment.repository;

import java.util.UUID;

import com.smarthome.commerce.payment.model.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {
}
