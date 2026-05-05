package com.smarthome.commerce.api.warehouse;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.UUID;

import com.smarthome.commerce.api.cart.ShoppingCartDto;
import org.junit.jupiter.api.Test;

class WarehouseFeignFallbackFactoryTest {

    private final WarehouseFeignFallbackFactory fallbackFactory = new WarehouseFeignFallbackFactory();

    @Test
    void fallbackReportsWarehouseUnavailableForCartStockChecks() {
        Throwable cause = new IllegalStateException("connection refused");
        WarehouseApi fallback = fallbackFactory.create(cause);
        ShoppingCartDto cart = new ShoppingCartDto(UUID.randomUUID(), Map.of(UUID.randomUUID(), 1L));

        assertThatThrownBy(() -> fallback.checkProductQuantityEnoughForShoppingCart(cart))
                .isInstanceOf(WarehouseServiceUnavailableException.class)
                .hasMessage("Warehouse service is temporarily unavailable. Please try again later.")
                .hasCause(cause);
    }

    @Test
    void fallbackUsesSameUnavailableResponseForAllWarehouseCalls() {
        Throwable cause = new IllegalStateException("timeout");
        WarehouseApi fallback = fallbackFactory.create(cause);
        UUID productId = UUID.randomUUID();

        assertThatThrownBy(fallback::getWarehouseAddress)
                .isInstanceOf(WarehouseServiceUnavailableException.class)
                .hasCause(cause);
        assertThatThrownBy(() -> fallback.addProductToWarehouse(new AddProductToWarehouseRequest(productId, 1L)))
                .isInstanceOf(WarehouseServiceUnavailableException.class)
                .hasCause(cause);
        assertThatThrownBy(() -> fallback.newProductInWarehouse(new NewProductInWarehouseRequest(
                productId,
                false,
                new DimensionDto(1.0, 1.0, 1.0),
                1.0
        )))
                .isInstanceOf(WarehouseServiceUnavailableException.class)
                .hasCause(cause);
    }
}
