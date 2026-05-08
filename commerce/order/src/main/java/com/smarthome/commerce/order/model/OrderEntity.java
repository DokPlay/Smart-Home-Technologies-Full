package com.smarthome.commerce.order.model;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.smarthome.commerce.api.order.OrderState;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    private UUID orderId;
    private String username;
    private UUID shoppingCartId;
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "product_id")
    private Map<UUID, Long> products = new LinkedHashMap<>();
    private UUID paymentId;
    private UUID deliveryId;
    @Enumerated(EnumType.STRING)
    private OrderState state;
    private Double deliveryWeight;
    private Double deliveryVolume;
    private Boolean fragile;
    private BigDecimal totalPrice;
    private BigDecimal deliveryPrice;
    private BigDecimal productPrice;
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

    protected OrderEntity() {
    }

    public OrderEntity(UUID orderId, String username, UUID shoppingCartId, Map<UUID, Long> products,
                       AddressEmbeddable fromAddress, AddressEmbeddable toAddress, Double deliveryWeight,
                       Double deliveryVolume, Boolean fragile) {
        this.orderId = orderId;
        this.username = username;
        this.shoppingCartId = shoppingCartId;
        this.products = new LinkedHashMap<>(products);
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.deliveryWeight = deliveryWeight;
        this.deliveryVolume = deliveryVolume;
        this.fragile = fragile;
        this.state = OrderState.NEW;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public String getUsername() {
        return username;
    }

    public UUID getShoppingCartId() {
        return shoppingCartId;
    }

    public Map<UUID, Long> getProducts() {
        return products;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public UUID getDeliveryId() {
        return deliveryId;
    }

    public OrderState getState() {
        return state;
    }

    public Double getDeliveryWeight() {
        return deliveryWeight;
    }

    public Double getDeliveryVolume() {
        return deliveryVolume;
    }

    public Boolean getFragile() {
        return fragile;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public BigDecimal getDeliveryPrice() {
        return deliveryPrice;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public AddressEmbeddable getFromAddress() {
        return fromAddress;
    }

    public AddressEmbeddable getToAddress() {
        return toAddress;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public void setDeliveryId(UUID deliveryId) {
        this.deliveryId = deliveryId;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public void setDeliveryWeight(Double deliveryWeight) {
        this.deliveryWeight = deliveryWeight;
    }

    public void setDeliveryVolume(Double deliveryVolume) {
        this.deliveryVolume = deliveryVolume;
    }

    public void setFragile(Boolean fragile) {
        this.fragile = fragile;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setDeliveryPrice(BigDecimal deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }
}
