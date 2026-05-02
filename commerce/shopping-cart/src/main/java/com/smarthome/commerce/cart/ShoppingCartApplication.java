package com.smarthome.commerce.cart;

import com.smarthome.commerce.api.warehouse.WarehouseApi;
import com.smarthome.commerce.api.warehouse.WarehouseFeignFallbackFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableFeignClients(clients = WarehouseApi.class)
@Import(WarehouseFeignFallbackFactory.class)
public class ShoppingCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartApplication.class, args);
    }
}
