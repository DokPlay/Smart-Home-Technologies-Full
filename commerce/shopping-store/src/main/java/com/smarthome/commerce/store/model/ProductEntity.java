package com.smarthome.commerce.store.model;

import java.math.BigDecimal;
import java.util.UUID;

import com.smarthome.commerce.api.store.ProductCategory;
import com.smarthome.commerce.api.store.ProductState;
import com.smarthome.commerce.api.store.QuantityState;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    private UUID productId;
    private String productName;
    private String description;
    private String imageSrc;
    @Enumerated(EnumType.STRING)
    private QuantityState quantityState;
    @Enumerated(EnumType.STRING)
    private ProductState productState;
    private Double rating;
    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;
    private BigDecimal price;

    protected ProductEntity() {
    }

    public ProductEntity(UUID productId, String productName, String description, String imageSrc,
                         QuantityState quantityState, ProductState productState, Double rating,
                         ProductCategory productCategory, BigDecimal price) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.imageSrc = imageSrc;
        this.quantityState = quantityState;
        this.productState = productState;
        this.rating = rating;
        this.productCategory = productCategory;
        this.price = price;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public QuantityState getQuantityState() {
        return quantityState;
    }

    public void setQuantityState(QuantityState quantityState) {
        this.quantityState = quantityState;
    }

    public ProductState getProductState() {
        return productState;
    }

    public void setProductState(ProductState productState) {
        this.productState = productState;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
