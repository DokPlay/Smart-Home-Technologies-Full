package com.smarthome.commerce.payment.client;

import com.smarthome.commerce.payment.exception.ShoppingStoreServiceUnavailableException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {

    @Override
    public ProductClient create(Throwable cause) {
        return productId -> {
            throw new ShoppingStoreServiceUnavailableException(cause);
        };
    }
}
