package com.smarthome.commerce.store.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import com.smarthome.commerce.api.store.ProductCategory;
import com.smarthome.commerce.api.store.ProductDto;
import com.smarthome.commerce.api.store.ProductState;
import com.smarthome.commerce.api.store.QuantityState;
import com.smarthome.commerce.api.store.SetProductQuantityStateRequest;
import org.springframework.data.domain.PageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:shopping-store-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
})
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    void createNewProductStoresActiveProductAndReturnsItByCategory() {
        ProductDto created = productService.createNewProduct(new ProductDto(
                null,
                "Smart Hub",
                "Central smart home controller",
                "image/src/hub",
                QuantityState.MANY,
                ProductState.ACTIVE,
                4.8,
                ProductCategory.CONTROL,
                BigDecimal.valueOf(199.99)
        ));

        assertThat(created.productId()).isNotNull();
        assertThat(productService.getProduct(created.productId()).productName()).isEqualTo("Smart Hub");
        assertThat(productService.getProducts(ProductCategory.CONTROL, PageRequest.of(0, 20)))
                .extracting(ProductDto::productId)
                .contains(created.productId());
    }

    @Test
    void createNewProductPreservesProvidedProductIdForCrossServiceUse() {
        UUID productId = UUID.randomUUID();

        ProductDto created = productService.createNewProduct(new ProductDto(
                productId,
                "Smart Relay",
                "Relay used by warehouse and store with the same id",
                null,
                QuantityState.ENOUGH,
                ProductState.ACTIVE,
                4.7,
                ProductCategory.CONTROL,
                BigDecimal.valueOf(79.90)
        ));

        assertThat(created.productId()).isEqualTo(productId);
        assertThat(productService.getProduct(productId).productName()).isEqualTo("Smart Relay");
    }

    @Test
    void removeProductFromStoreSoftDeactivatesProduct() {
        ProductDto created = productService.createNewProduct(new ProductDto(
                null,
                "Window Sensor",
                "Door and window opening detector",
                null,
                QuantityState.ENOUGH,
                ProductState.ACTIVE,
                4.3,
                ProductCategory.SENSORS,
                BigDecimal.valueOf(49.90)
        ));

        assertThat(productService.removeProductFromStore(created.productId())).isTrue();

        assertThat(productService.getProduct(created.productId()).productState()).isEqualTo(ProductState.DEACTIVATE);
        assertThat(productService.getProducts(ProductCategory.SENSORS, PageRequest.of(0, 20)))
                .extracting(ProductDto::productId)
                .doesNotContain(created.productId());
    }

    @Test
    void setProductQuantityStateUpdatesOnlyAvailabilityStatus() {
        ProductDto created = productService.createNewProduct(new ProductDto(
                null,
                "Smart Bulb",
                "Dimmable lamp",
                null,
                QuantityState.FEW,
                ProductState.ACTIVE,
                4.6,
                ProductCategory.LIGHTING,
                BigDecimal.valueOf(24.50)
        ));

        assertThat(productService.setProductQuantityState(
                new SetProductQuantityStateRequest(created.productId(), QuantityState.ENDED)
        )).isTrue();

        ProductDto updated = productService.getProduct(created.productId());
        assertThat(updated.quantityState()).isEqualTo(QuantityState.ENDED);
        assertThat(updated.productState()).isEqualTo(ProductState.ACTIVE);
        assertThat(updated.productName()).isEqualTo("Smart Bulb");
    }
}
