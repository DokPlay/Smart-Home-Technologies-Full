package com.smarthome.commerce.delivery.model;

import java.util.UUID;

import com.smarthome.commerce.api.delivery.DeliveryState;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "deliveries")
public class DeliveryEntity {

    @Id
    private UUID deliveryId;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "from_country")),
            @AttributeOverride(name = "city", column = @Column(name = "from_city")),
            @AttributeOverride(name = "street", column = @Column(name = "from_street")),
            @AttributeOverride(name = "house", column = @Column(name = "from_house")),
            @AttributeOverride(name = "flat", column = @Column(name = "from_flat"))
    })
    private AddressEmbeddable fromAddress;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "to_country")),
            @AttributeOverride(name = "city", column = @Column(name = "to_city")),
            @AttributeOverride(name = "street", column = @Column(name = "to_street")),
            @AttributeOverride(name = "house", column = @Column(name = "to_house")),
            @AttributeOverride(name = "flat", column = @Column(name = "to_flat"))
    })
    private AddressEmbeddable toAddress;
    private UUID orderId;
    @Enumerated(EnumType.STRING)
    private DeliveryState deliveryState;

    protected DeliveryEntity() {
    }

    public DeliveryEntity(UUID deliveryId, AddressEmbeddable fromAddress, AddressEmbeddable toAddress, UUID orderId,
                          DeliveryState deliveryState) {
        this.deliveryId = deliveryId;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.orderId = orderId;
        this.deliveryState = deliveryState;
    }

    public UUID getDeliveryId() {
        return deliveryId;
    }

    public AddressEmbeddable getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(AddressEmbeddable fromAddress) {
        this.fromAddress = fromAddress;
    }

    public AddressEmbeddable getToAddress() {
        return toAddress;
    }

    public void setToAddress(AddressEmbeddable toAddress) {
        this.toAddress = toAddress;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public DeliveryState getDeliveryState() {
        return deliveryState;
    }

    public void setDeliveryState(DeliveryState deliveryState) {
        this.deliveryState = deliveryState;
    }
}
