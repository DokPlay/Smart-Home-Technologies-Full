package com.smarthome.commerce.cart.feign;

import com.smarthome.commerce.api.cart.ShoppingCartDto;
import com.smarthome.commerce.api.warehouse.AddProductToWarehouseRequest;
import com.smarthome.commerce.api.warehouse.AddressDto;
import com.smarthome.commerce.api.warehouse.BookedProductsDto;
import com.smarthome.commerce.api.warehouse.NewProductInWarehouseRequest;
import com.smarthome.commerce.cart.exception.WarehouseServiceUnavailableException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class WarehouseFeignFallbackFactory implements FallbackFactory<WarehouseFeignClient> {

    @Override
    public WarehouseFeignClient create(Throwable cause) {
        return new WarehouseFeignFallback(cause);
    }

    private static class WarehouseFeignFallback implements WarehouseFeignClient {

        private final Throwable cause;

        private WarehouseFeignFallback(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public void newProductInWarehouse(NewProductInWarehouseRequest request) {
            throw unavailable();
        }

        @Override
        public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCart) {
            throw unavailable();
        }

        @Override
        public void addProductToWarehouse(AddProductToWarehouseRequest request) {
            throw unavailable();
        }

        @Override
        public AddressDto getWarehouseAddress() {
            throw unavailable();
        }

        private WarehouseServiceUnavailableException unavailable() {
            return new WarehouseServiceUnavailableException(cause);
        }
    }
}
