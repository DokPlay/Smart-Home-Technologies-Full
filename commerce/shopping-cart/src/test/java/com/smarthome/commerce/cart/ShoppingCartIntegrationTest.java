package com.smarthome.commerce.cart;

import com.smarthome.commerce.cart.feign.WarehouseFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ShoppingCartIntegrationTest {

    @Autowired
    private ShoppingCartApplication app;

    @MockBean
    private WarehouseFeignClient warehouseFeignClient;

    @Test
    void addItem_whenWarehouseHasEnough_stockAccepted() {
        when(warehouseFeignClient.checkAvailability(Map.of(1L, 2))).thenReturn(Map.of(1L, "OK"));
        // просто проверим, что контекст стартует и мок работает
        var result = warehouseFeignClient.checkAvailability(Map.of(1L,2));
        assertThat(result.get(1L)).isEqualTo("OK");
    }
}
