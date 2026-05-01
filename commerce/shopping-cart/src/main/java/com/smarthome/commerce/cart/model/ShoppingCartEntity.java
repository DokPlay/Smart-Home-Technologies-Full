package com.smarthome.commerce.cart.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "shopping_carts")
public class ShoppingCartEntity {

    @Id
    private UUID shoppingCartId;
    private String username;
    private boolean active;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ShoppingCartItemEntity> items = new ArrayList<>();

    protected ShoppingCartEntity() {
    }

    public ShoppingCartEntity(UUID shoppingCartId, String username, boolean active) {
        this.shoppingCartId = shoppingCartId;
        this.username = username;
        this.active = active;
    }

    public UUID getShoppingCartId() {
        return shoppingCartId;
    }

    public String getUsername() {
        return username;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }

    public List<ShoppingCartItemEntity> getItems() {
        return items;
    }

    public void addItem(UUID productId, long quantity) {
        ShoppingCartItemEntity item = items.stream()
                .filter(existing -> existing.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
        if (item == null) {
            items.add(new ShoppingCartItemEntity(UUID.randomUUID(), this, productId, quantity));
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
    }

    public void setItemQuantity(UUID productId, long quantity) {
        ShoppingCartItemEntity item = items.stream()
                .filter(existing -> existing.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
        if (item != null) {
            item.setQuantity(quantity);
        }
    }

    public void removeItem(UUID productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
    }
}
