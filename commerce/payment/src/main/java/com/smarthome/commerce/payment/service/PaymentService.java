package com.smarthome.commerce.payment.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import com.smarthome.commerce.api.order.OrderApi;
import com.smarthome.commerce.api.order.OrderDto;
import com.smarthome.commerce.api.payment.PaymentDto;
import com.smarthome.commerce.api.payment.PaymentState;
import com.smarthome.commerce.api.store.ProductDto;
import com.smarthome.commerce.payment.client.ProductClient;
import com.smarthome.commerce.payment.exception.NoPaymentFoundException;
import com.smarthome.commerce.payment.exception.NotEnoughInfoInOrderToCalculateException;
import com.smarthome.commerce.payment.model.PaymentEntity;
import com.smarthome.commerce.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private static final BigDecimal TAX_RATE = BigDecimal.valueOf(0.10);

    private final PaymentRepository paymentRepository;
    private final ProductClient productClient;
    private final OrderApi orderApi;

    public PaymentService(PaymentRepository paymentRepository, ProductClient productClient, OrderApi orderApi) {
        this.paymentRepository = paymentRepository;
        this.productClient = productClient;
        this.orderApi = orderApi;
    }

    @Transactional
    public PaymentDto payment(OrderDto order) {
        requireOrder(order);
        BigDecimal productTotal = productCost(order);
        BigDecimal deliveryTotal = money(order.deliveryPrice());
        BigDecimal feeTotal = productTotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPayment = productTotal.add(feeTotal).add(deliveryTotal).setScale(2, RoundingMode.HALF_UP);

        PaymentEntity payment = paymentRepository.save(new PaymentEntity(
                UUID.randomUUID(),
                order.orderId(),
                productTotal,
                deliveryTotal,
                feeTotal,
                totalPayment,
                PaymentState.PENDING
        ));
        return toDto(payment);
    }

    @Transactional(readOnly = true)
    public BigDecimal productCost(OrderDto order) {
        requireOrder(order);
        return order.products().entrySet().stream()
                .map(entry -> {
                    ProductDto product = productClient.getProduct(entry.getKey());
                    if (product.price() == null) {
                        throw new NotEnoughInfoInOrderToCalculateException("Product price is missing: " + entry.getKey());
                    }
                    return product.price().multiply(BigDecimal.valueOf(entry.getValue()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalCost(OrderDto order) {
        requireOrder(order);
        BigDecimal productTotal = order.productPrice() == null ? productCost(order) : money(order.productPrice());
        BigDecimal deliveryTotal = money(order.deliveryPrice());
        BigDecimal feeTotal = productTotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        return productTotal.add(feeTotal).add(deliveryTotal).setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public void paymentSuccess(UUID paymentId) {
        PaymentEntity payment = findPayment(paymentId);
        payment.setState(PaymentState.SUCCESS);
        orderApi.payment(payment.getOrderId());
    }

    @Transactional
    public void paymentFailed(UUID paymentId) {
        PaymentEntity payment = findPayment(paymentId);
        payment.setState(PaymentState.FAILED);
        orderApi.paymentFailed(payment.getOrderId());
    }

    private PaymentEntity findPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoPaymentFoundException(paymentId));
    }

    private void requireOrder(OrderDto order) {
        if (order == null || order.orderId() == null || order.products() == null || order.products().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("Order id and products must be filled");
        }
    }

    private BigDecimal money(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2) : value.setScale(2, RoundingMode.HALF_UP);
    }

    private PaymentDto toDto(PaymentEntity payment) {
        return new PaymentDto(payment.getPaymentId(), payment.getTotalPayment(), payment.getDeliveryTotal(),
                payment.getFeeTotal());
    }
}
