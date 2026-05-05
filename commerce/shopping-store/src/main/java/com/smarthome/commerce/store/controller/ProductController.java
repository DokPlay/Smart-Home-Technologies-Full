package com.smarthome.commerce.store.controller;

import java.util.Collection;
import java.util.UUID;

import com.smarthome.commerce.api.store.ProductApi;
import com.smarthome.commerce.api.store.ProductCategory;
import com.smarthome.commerce.api.store.ProductDto;
import com.smarthome.commerce.api.store.SetProductQuantityStateRequest;
import com.smarthome.commerce.store.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController implements ProductApi {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public Collection<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        return productService.getProducts(category, pageable);
    }

    @Override
    public ProductDto createNewProduct(ProductDto product) {
        return productService.createNewProduct(product);
    }

    @Override
    public ProductDto updateProduct(ProductDto product) {
        return productService.updateProduct(product);
    }

    @Override
    public Boolean removeProductFromStore(UUID productId) {
        return productService.removeProductFromStore(productId);
    }

    @Override
    public Boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        return productService.setProductQuantityState(request);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        return productService.getProduct(productId);
    }
}
