package com.smarthome.commerce.payment.model;

import java.math.BigDecimal;
import java.util.UUID;

import com.smarthome.commerce.api.payment.PaymentState;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    private UUID paymentId;
    private UUID orderId;
    private BigDecimal productTotal;
    private BigDecimal deliveryTotal;
    private BigDecimal feeTotal;
    private BigDecimal totalPayment;
    @Enumerated(EnumType.STRING)
    private PaymentState state;

    protected PaymentEntity() {
    }

    public PaymentEntity(UUID paymentId, UUID orderId, BigDecimal productTotal, BigDecimal deliveryTotal,
                         BigDecimal feeTotal, BigDecimal totalPayment, PaymentState state) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.productTotal = productTotal;
        this.deliveryTotal = deliveryTotal;
        this.feeTotal = feeTotal;
        this.totalPayment = totalPayment;
        this.state = state;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public BigDecimal getDeliveryTotal() {
        return deliveryTotal;
    }

    public BigDecimal getFeeTotal() {
        return feeTotal;
    }

    public BigDecimal getTotalPayment() {
        return totalPayment;
    }

    public void setState(PaymentState state) {
        this.state = state;
    }
}
