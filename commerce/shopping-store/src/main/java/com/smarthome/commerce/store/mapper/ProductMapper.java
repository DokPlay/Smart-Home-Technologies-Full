package com.smarthome.commerce.store.mapper;

import com.smarthome.commerce.api.store.ProductDto;
import com.smarthome.commerce.store.model.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDto toDto(ProductEntity product) {
        return new ProductDto(
                product.getProductId(),
                product.getProductName(),
                product.getDescription(),
                product.getImageSrc(),
                product.getQuantityState(),
                product.getProductState(),
                product.getRating(),
                product.getProductCategory(),
                product.getPrice()
        );
    }

    public ProductEntity toEntity(ProductDto product) {
        return new ProductEntity(
                product.productId(),
                product.productName(),
                product.description(),
                product.imageSrc(),
                product.quantityState(),
                product.productState(),
                product.rating(),
                product.productCategory(),
                product.price()
        );
    }
}
