package com.smarthome.commerce.delivery;

import com.smarthome.commerce.api.order.OrderApi;
import com.smarthome.commerce.api.order.OrderFeignFallbackFactory;
import com.smarthome.commerce.api.warehouse.WarehouseApi;
import com.smarthome.commerce.api.warehouse.WarehouseFeignFallbackFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableFeignClients(clients = {OrderApi.class, WarehouseApi.class})
@Import({OrderFeignFallbackFactory.class, WarehouseFeignFallbackFactory.class})
public class DeliveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryApplication.class, args);
    }
}
