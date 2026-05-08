package com.smarthome.commerce.order;

import com.smarthome.commerce.api.delivery.DeliveryApi;
import com.smarthome.commerce.api.delivery.DeliveryFeignFallbackFactory;
import com.smarthome.commerce.api.payment.PaymentApi;
import com.smarthome.commerce.api.payment.PaymentFeignFallbackFactory;
import com.smarthome.commerce.api.warehouse.WarehouseApi;
import com.smarthome.commerce.api.warehouse.WarehouseFeignFallbackFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableFeignClients(clients = {WarehouseApi.class, DeliveryApi.class, PaymentApi.class})
@Import({WarehouseFeignFallbackFactory.class, DeliveryFeignFallbackFactory.class, PaymentFeignFallbackFactory.class})
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
