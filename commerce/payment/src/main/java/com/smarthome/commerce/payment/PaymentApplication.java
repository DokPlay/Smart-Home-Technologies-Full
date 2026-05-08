package com.smarthome.commerce.payment;

import com.smarthome.commerce.api.order.OrderApi;
import com.smarthome.commerce.api.order.OrderFeignFallbackFactory;
import com.smarthome.commerce.payment.client.ProductClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableFeignClients(clients = {OrderApi.class, ProductClient.class})
@Import(OrderFeignFallbackFactory.class)
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}
