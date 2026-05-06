package com.smarthome.commerce.payment.client;

import java.util.UUID;

import com.smarthome.commerce.api.store.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "shopping-store", fallbackFactory = ProductClientFallbackFactory.class)
public interface ProductClient {

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);
}
