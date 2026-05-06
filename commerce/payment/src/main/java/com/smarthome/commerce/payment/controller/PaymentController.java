package com.smarthome.commerce.payment.controller;

import java.math.BigDecimal;
import java.util.UUID;

import com.smarthome.commerce.api.order.OrderDto;
import com.smarthome.commerce.api.payment.PaymentApi;
import com.smarthome.commerce.api.payment.PaymentDto;
import com.smarthome.commerce.payment.service.PaymentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public PaymentDto payment(OrderDto order) {
        return paymentService.payment(order);
    }

    @Override
    public BigDecimal getTotalCost(OrderDto order) {
        return paymentService.getTotalCost(order);
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        paymentService.paymentSuccess(paymentId);
    }

    @Override
    public BigDecimal productCost(OrderDto order) {
        return paymentService.productCost(order);
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        paymentService.paymentFailed(paymentId);
    }
}
