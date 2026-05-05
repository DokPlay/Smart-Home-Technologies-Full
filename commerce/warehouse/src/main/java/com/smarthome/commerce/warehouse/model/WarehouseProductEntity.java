package com.smarthome.commerce.warehouse.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "warehouse_products")
public class WarehouseProductEntity {

    @Id
    private UUID productId;
    private boolean fragile;
    private double width;
    private double height;
    private double depth;
    private double weight;
    private long quantity;

    protected WarehouseProductEntity() {
    }

    public WarehouseProductEntity(UUID productId, boolean fragile, double width, double height, double depth,
                                  double weight, long quantity) {
        this.productId = productId;
        this.fragile = fragile;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.weight = weight;
        this.quantity = quantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public boolean isFragile() {
        return fragile;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getDepth() {
        return depth;
    }

    public double getWeight() {
        return weight;
    }

    public long getQuantity() {
        return quantity;
    }

    public void addQuantity(long quantity) {
        this.quantity += quantity;
    }
}
