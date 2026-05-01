package com.smarthome.commerce.cart;

import com.smarthome.commerce.api.cart.ShoppingCartDto;
import com.smarthome.commerce.api.warehouse.BookedProductsDto;
import com.smarthome.commerce.cart.feign.WarehouseFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:shopping-cart-integration-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false"
})
public class ShoppingCartIntegrationTest {

    @Autowired
    private ShoppingCartApplication app;

    @MockBean
    private WarehouseFeignClient warehouseFeignClient;

    @Test
    void addItem_whenWarehouseHasEnough_stockAccepted() {
        UUID productId = UUID.randomUUID();
        ShoppingCartDto cart = new ShoppingCartDto(UUID.randomUUID(), Map.of(productId, 2L));
        when(warehouseFeignClient.checkProductQuantityEnoughForShoppingCart(cart))
                .thenReturn(new BookedProductsDto(2.0, 4.0, false));

        var result = warehouseFeignClient.checkProductQuantityEnoughForShoppingCart(cart);

        assertThat(result.deliveryWeight()).isEqualTo(2.0);
    }
}
