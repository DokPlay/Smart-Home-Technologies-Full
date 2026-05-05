package com.smarthome.commerce.warehouse.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.UUID;

import com.smarthome.commerce.api.cart.ShoppingCartDto;
import com.smarthome.commerce.api.warehouse.AddProductToWarehouseRequest;
import com.smarthome.commerce.api.warehouse.AddressDto;
import com.smarthome.commerce.api.warehouse.BookedProductsDto;
import com.smarthome.commerce.api.warehouse.DimensionDto;
import com.smarthome.commerce.api.warehouse.NewProductInWarehouseRequest;
import com.smarthome.commerce.warehouse.exception.InvalidWarehouseProductRequestException;
import com.smarthome.commerce.warehouse.exception.InvalidWarehouseProductQuantityException;
import com.smarthome.commerce.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:warehouse-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
})
class WarehouseServiceTest {

    @Autowired
    private WarehouseService warehouseService;

    @Test
    void checkProductQuantityCalculatesBookingWeightVolumeAndFragility() {
        UUID productId = UUID.randomUUID();
        warehouseService.newProductInWarehouse(new NewProductInWarehouseRequest(
                productId,
                true,
                new DimensionDto(2.0, 3.0, 4.0),
                1.5
        ));
        warehouseService.addProductToWarehouse(new AddProductToWarehouseRequest(productId, 3L));

        BookedProductsDto booking = warehouseService.checkProductQuantityEnoughForShoppingCart(
                new ShoppingCartDto(UUID.randomUUID(), Map.of(productId, 2L))
        );

        assertThat(booking.deliveryWeight()).isEqualTo(3.0);
        assertThat(booking.deliveryVolume()).isEqualTo(48.0);
        assertThat(booking.fragile()).isTrue();
    }

    @Test
    void checkProductQuantityReportsProductThatCannotBeBooked() {
        UUID productId = UUID.randomUUID();
        warehouseService.newProductInWarehouse(new NewProductInWarehouseRequest(
                productId,
                false,
                new DimensionDto(1.0, 1.0, 1.0),
                1.0
        ));

        assertThatThrownBy(() -> warehouseService.checkProductQuantityEnoughForShoppingCart(
                new ShoppingCartDto(UUID.randomUUID(), Map.of(productId, 2L))
        ))
                .isInstanceOf(ProductInShoppingCartLowQuantityInWarehouseException.class)
                .hasMessageContaining(productId.toString());
    }

    @Test
    void addProductToWarehouseRejectsNonPositiveQuantity() {
        UUID productId = UUID.randomUUID();
        warehouseService.newProductInWarehouse(new NewProductInWarehouseRequest(
                productId,
                false,
                new DimensionDto(1.0, 1.0, 1.0),
                1.0
        ));

        assertThatThrownBy(() -> warehouseService.addProductToWarehouse(
                new AddProductToWarehouseRequest(productId, 0L)
        ))
                .isInstanceOf(InvalidWarehouseProductQuantityException.class);
    }

    @Test
    void newProductInWarehouseRejectsMissingDimensions() {
        assertThatThrownBy(() -> warehouseService.newProductInWarehouse(new NewProductInWarehouseRequest(
                UUID.randomUUID(),
                false,
                null,
                1.0
        )))
                .isInstanceOf(InvalidWarehouseProductRequestException.class);
    }

    @Test
    void newProductInWarehouseRejectsDuplicateProductId() {
        UUID productId = UUID.randomUUID();
        NewProductInWarehouseRequest request = new NewProductInWarehouseRequest(
                productId,
                false,
                new DimensionDto(1.0, 1.0, 1.0),
                1.0
        );
        warehouseService.newProductInWarehouse(request);

        assertThatThrownBy(() -> warehouseService.newProductInWarehouse(request))
                .hasMessageContaining(productId.toString());
    }

    @Test
    void getWarehouseAddressDuplicatesSelectedWarehouseAliasInAllFields() {
        AddressDto address = warehouseService.getWarehouseAddress();

        assertThat(address.country()).isIn("ADDRESS_1", "ADDRESS_2");
        assertThat(address.city()).isEqualTo(address.country());
        assertThat(address.street()).isEqualTo(address.country());
        assertThat(address.house()).isEqualTo(address.country());
        assertThat(address.flat()).isEqualTo(address.country());
    }
}
