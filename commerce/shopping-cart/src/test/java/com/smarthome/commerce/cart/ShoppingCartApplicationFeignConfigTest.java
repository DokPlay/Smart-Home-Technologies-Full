package com.smarthome.commerce.cart;

import static org.assertj.core.api.Assertions.assertThat;

import com.smarthome.commerce.api.warehouse.WarehouseApi;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.EnableFeignClients;

class ShoppingCartApplicationFeignConfigTest {

    @Test
    void shoppingCartApplicationEnablesOnlyRequiredFeignClientExplicitly() {
        EnableFeignClients enableFeignClients = ShoppingCartApplication.class.getAnnotation(EnableFeignClients.class);

        assertThat(enableFeignClients).isNotNull();
        assertThat(enableFeignClients.clients()).containsExactly(WarehouseApi.class);
    }
}
