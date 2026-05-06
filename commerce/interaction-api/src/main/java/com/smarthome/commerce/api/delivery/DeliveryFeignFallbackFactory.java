package com.smarthome.commerce.api.delivery;

import java.math.BigDecimal;
import java.util.UUID;

import com.smarthome.commerce.api.order.OrderDto;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class DeliveryFeignFallbackFactory implements FallbackFactory<DeliveryApi> {

    @Override
    public DeliveryApi create(Throwable cause) {
        return new DeliveryApiFallback(cause);
    }

    private static class DeliveryApiFallback implements DeliveryApi {

        private final Throwable cause;

        private DeliveryApiFallback(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public DeliveryDto planDelivery(DeliveryDto delivery) {
            throw unavailable();
        }

        @Override
        public void deliverySuccessful(UUID orderId) {
            throw unavailable();
        }

        @Override
        public void deliveryPicked(UUID orderId) {
            throw unavailable();
        }

        @Override
        public void deliveryFailed(UUID orderId) {
            throw unavailable();
        }

        @Override
        public BigDecimal deliveryCost(OrderDto order) {
            throw unavailable();
        }

        private DeliveryServiceUnavailableException unavailable() {
            return new DeliveryServiceUnavailableException(cause);
        }
    }
}
