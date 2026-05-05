package com.smarthome.commerce.cart.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "shopping_cart_items")
public class ShoppingCartItemEntity {

    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_cart_id")
    private ShoppingCartEntity cart;
    private UUID productId;
    private long quantity;

    protected ShoppingCartItemEntity() {
    }

    public ShoppingCartItemEntity(UUID id, ShoppingCartEntity cart, UUID productId, long quantity) {
        this.id = id;
        this.cart = cart;
        this.productId = productId;
        this.quantity = quantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}
