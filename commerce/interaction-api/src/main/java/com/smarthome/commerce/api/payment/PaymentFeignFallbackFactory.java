package com.smarthome.commerce.api.payment;

import java.math.BigDecimal;
import java.util.UUID;

import com.smarthome.commerce.api.order.OrderDto;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class PaymentFeignFallbackFactory implements FallbackFactory<PaymentApi> {

    @Override
    public PaymentApi create(Throwable cause) {
        return new PaymentApiFallback(cause);
    }

    private static class PaymentApiFallback implements PaymentApi {

        private final Throwable cause;

        private PaymentApiFallback(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public PaymentDto payment(OrderDto order) {
            throw unavailable();
        }

        @Override
        public BigDecimal getTotalCost(OrderDto order) {
            throw unavailable();
        }

        @Override
        public void paymentSuccess(UUID paymentId) {
            throw unavailable();
        }

        @Override
        public BigDecimal productCost(OrderDto order) {
            throw unavailable();
        }

        @Override
        public void paymentFailed(UUID paymentId) {
            throw unavailable();
        }

        private PaymentServiceUnavailableException unavailable() {
            return new PaymentServiceUnavailableException(cause);
        }
    }
}
