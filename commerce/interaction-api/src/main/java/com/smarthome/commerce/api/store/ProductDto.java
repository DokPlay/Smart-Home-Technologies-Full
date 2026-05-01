package com.smarthome.commerce.api.store;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(
        UUID productId,
        String productName,
        String description,
        String imageSrc,
        QuantityState quantityState,
        ProductState productState,
        Double rating,
        ProductCategory productCategory,
        BigDecimal price
) {
}
