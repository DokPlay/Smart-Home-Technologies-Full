package com.smarthome.commerce.order.controller;

import java.util.List;
import java.util.UUID;

import com.smarthome.commerce.api.order.CreateNewOrderRequest;
import com.smarthome.commerce.api.order.OrderApi;
import com.smarthome.commerce.api.order.OrderDto;
import com.smarthome.commerce.api.order.ProductReturnRequest;
import com.smarthome.commerce.order.service.OrderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController implements OrderApi {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public List<OrderDto> getClientOrders(String username) {
        return orderService.getClientOrders(username);
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        return orderService.createNewOrder(request);
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        return orderService.productReturn(request);
    }

    @Override
    public OrderDto payment(UUID orderId) {
        return orderService.payment(orderId);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        return orderService.paymentFailed(orderId);
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        return orderService.delivery(orderId);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        return orderService.deliveryFailed(orderId);
    }

    @Override
    public OrderDto complete(UUID orderId) {
        return orderService.complete(orderId);
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        return orderService.calculateTotalCost(orderId);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        return orderService.calculateDeliveryCost(orderId);
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        return orderService.assembly(orderId);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        return orderService.assemblyFailed(orderId);
    }
}
