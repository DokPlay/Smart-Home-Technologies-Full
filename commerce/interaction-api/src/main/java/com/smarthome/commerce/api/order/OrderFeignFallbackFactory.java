package com.smarthome.commerce.api.order;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderFeignFallbackFactory implements FallbackFactory<OrderApi> {

    @Override
    public OrderApi create(Throwable cause) {
        return new OrderApiFallback(cause);
    }

    private static class OrderApiFallback implements OrderApi {

        private final Throwable cause;

        private OrderApiFallback(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public List<OrderDto> getClientOrders(String username) {
            throw unavailable();
        }

        @Override
        public OrderDto createNewOrder(CreateNewOrderRequest request) {
            throw unavailable();
        }

        @Override
        public OrderDto productReturn(ProductReturnRequest request) {
            throw unavailable();
        }

        @Override
        public OrderDto payment(UUID orderId) {
            throw unavailable();
        }

        @Override
        public OrderDto paymentFailed(UUID orderId) {
            throw unavailable();
        }

        @Override
        public OrderDto delivery(UUID orderId) {
            throw unavailable();
        }

        @Override
        public OrderDto deliveryFailed(UUID orderId) {
            throw unavailable();
        }

        @Override
        public OrderDto complete(UUID orderId) {
            throw unavailable();
        }

        @Override
        public OrderDto calculateTotalCost(UUID orderId) {
            throw unavailable();
        }

        @Override
        public OrderDto calculateDeliveryCost(UUID orderId) {
            throw unavailable();
        }

        @Override
        public OrderDto assembly(UUID orderId) {
            throw unavailable();
        }

        @Override
        public OrderDto assemblyFailed(UUID orderId) {
            throw unavailable();
        }

        private OrderServiceUnavailableException unavailable() {
            return new OrderServiceUnavailableException(cause);
        }
    }
}
