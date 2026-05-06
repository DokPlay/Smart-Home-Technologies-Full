package com.smarthome.commerce.warehouse.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_bookings")
public class OrderBookingEntity {

    @Id
    private UUID orderId;
    private UUID deliveryId;
    @ElementCollection
    @MapKeyColumn(name = "product_id")
    private Map<UUID, Long> products = new LinkedHashMap<>();

    protected OrderBookingEntity() {
    }

    public OrderBookingEntity(UUID orderId, Map<UUID, Long> products) {
        this.orderId = orderId;
        this.products = new LinkedHashMap<>(products);
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setDeliveryId(UUID deliveryId) {
        this.deliveryId = deliveryId;
    }
}
