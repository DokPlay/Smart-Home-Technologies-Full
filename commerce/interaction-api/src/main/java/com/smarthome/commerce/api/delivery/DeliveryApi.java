package com.smarthome.commerce.api.delivery;

import java.math.BigDecimal;
import java.util.UUID;

import com.smarthome.commerce.api.order.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "delivery", path = "/api/v1/delivery", fallbackFactory = DeliveryFeignFallbackFactory.class)
public interface DeliveryApi {

    @PutMapping
    DeliveryDto planDelivery(@RequestBody DeliveryDto delivery);

    @PostMapping("/successful")
    void deliverySuccessful(@RequestBody UUID orderId);

    @PostMapping("/picked")
    void deliveryPicked(@RequestBody UUID orderId);

    @PostMapping("/failed")
    void deliveryFailed(@RequestBody UUID orderId);

    @PostMapping("/cost")
    BigDecimal deliveryCost(@RequestBody OrderDto order);
}
