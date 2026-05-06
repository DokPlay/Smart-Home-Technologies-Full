package com.smarthome.commerce.api.payment;

import java.math.BigDecimal;
import java.util.UUID;

import com.smarthome.commerce.api.order.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment", path = "/api/v1/payment", fallbackFactory = PaymentFeignFallbackFactory.class)
public interface PaymentApi {

    @PostMapping
    PaymentDto payment(@RequestBody OrderDto order);

    @PostMapping("/totalCost")
    BigDecimal getTotalCost(@RequestBody OrderDto order);

    @PostMapping("/refund")
    void paymentSuccess(@RequestBody UUID paymentId);

    @PostMapping("/productCost")
    BigDecimal productCost(@RequestBody OrderDto order);

    @PostMapping("/failed")
    void paymentFailed(@RequestBody UUID paymentId);
}
