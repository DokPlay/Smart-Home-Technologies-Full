package com.smarthome.commerce.delivery.controller;

import java.math.BigDecimal;
import java.util.UUID;

import com.smarthome.commerce.api.delivery.DeliveryApi;
import com.smarthome.commerce.api.delivery.DeliveryDto;
import com.smarthome.commerce.api.order.OrderDto;
import com.smarthome.commerce.delivery.service.DeliveryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/delivery")
public class DeliveryController implements DeliveryApi {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Override
    public DeliveryDto planDelivery(DeliveryDto delivery) {
        return deliveryService.planDelivery(delivery);
    }

    @Override
    public void deliverySuccessful(UUID orderId) {
        deliveryService.deliverySuccessful(orderId);
    }

    @Override
    public void deliveryPicked(UUID orderId) {
        deliveryService.deliveryPicked(orderId);
    }

    @Override
    public void deliveryFailed(UUID orderId) {
        deliveryService.deliveryFailed(orderId);
    }

    @Override
    public BigDecimal deliveryCost(OrderDto order) {
        return deliveryService.deliveryCost(order);
    }
}
