package com.smarthome.commerce.api.warehouse;

import com.smarthome.commerce.api.cart.ShoppingCartDto;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class WarehouseFeignFallbackFactory implements FallbackFactory<WarehouseApi> {

    @Override
    public WarehouseApi create(Throwable cause) {
        return new WarehouseApiFallback(cause);
    }

    private static class WarehouseApiFallback implements WarehouseApi {

        private final Throwable cause;

        private WarehouseApiFallback(Throwable cause) {
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
