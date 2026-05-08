package com.smarthome.commerce.order.service;

import java.util.List;
import java.util.UUID;

import com.smarthome.commerce.api.delivery.DeliveryApi;
import com.smarthome.commerce.api.delivery.DeliveryDto;
import com.smarthome.commerce.api.delivery.DeliveryState;
import com.smarthome.commerce.api.order.CreateNewOrderRequest;
import com.smarthome.commerce.api.order.OrderDto;
import com.smarthome.commerce.api.order.OrderState;
import com.smarthome.commerce.api.order.ProductReturnRequest;
import com.smarthome.commerce.api.payment.PaymentApi;
import com.smarthome.commerce.api.payment.PaymentDto;
import com.smarthome.commerce.api.warehouse.AddressDto;
import com.smarthome.commerce.api.warehouse.AssemblyProductsForOrderRequest;
import com.smarthome.commerce.api.warehouse.BookedProductsDto;
import com.smarthome.commerce.api.warehouse.WarehouseApi;
import com.smarthome.commerce.order.exception.InvalidOrderRequestException;
import com.smarthome.commerce.order.exception.NoOrderFoundException;
import com.smarthome.commerce.order.exception.NotAuthorizedUserException;
import com.smarthome.commerce.order.model.AddressEmbeddable;
import com.smarthome.commerce.order.model.OrderEntity;
import com.smarthome.commerce.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final WarehouseApi warehouseApi;
    private final DeliveryApi deliveryApi;
    private final PaymentApi paymentApi;

    public OrderService(OrderRepository orderRepository, WarehouseApi warehouseApi, DeliveryApi deliveryApi,
                        PaymentApi paymentApi) {
        this.orderRepository = orderRepository;
        this.warehouseApi = warehouseApi;
        this.deliveryApi = deliveryApi;
        this.paymentApi = paymentApi;
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getClientOrders(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException();
        }
        return orderRepository.findByUsername(username).stream().map(this::toDto).toList();
    }

    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        validateCreateRequest(request);
        UUID orderId = UUID.randomUUID();
        BookedProductsDto bookedProducts = warehouseApi.checkProductQuantityEnoughForShoppingCart(request.shoppingCart());
        AddressDto warehouseAddress = warehouseApi.getWarehouseAddress();

        OrderEntity order = new OrderEntity(
                orderId,
                request.username(),
                request.shoppingCart().shoppingCartId(),
                request.shoppingCart().products(),
                new AddressEmbeddable(warehouseAddress),
                new AddressEmbeddable(request.deliveryAddress()),
                bookedProducts.deliveryWeight(),
                bookedProducts.deliveryVolume(),
                bookedProducts.fragile()
        );
        DeliveryDto delivery = deliveryApi.planDelivery(new DeliveryDto(
                null,
                warehouseAddress,
                request.deliveryAddress(),
                orderId,
                DeliveryState.CREATED
        ));
        order.setDeliveryId(delivery.deliveryId());
        return toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto assembly(UUID orderId) {
        OrderEntity order = findOrder(orderId);
        if (order.getState() == OrderState.ASSEMBLED) {
            return toDto(order);
        }
        BookedProductsDto bookedProducts = warehouseApi.assemblyProductsForOrder(
                new AssemblyProductsForOrderRequest(order.getProducts(), order.getOrderId())
        );
        order.setDeliveryWeight(bookedProducts.deliveryWeight());
        order.setDeliveryVolume(bookedProducts.deliveryVolume());
        order.setFragile(bookedProducts.fragile());
        order.setState(OrderState.ASSEMBLED);
        return toDto(order);
    }

    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        OrderEntity order = findOrder(orderId);
        order.setDeliveryPrice(deliveryApi.deliveryCost(toDto(order)));
        return toDto(order);
    }

    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        OrderEntity order = findOrder(orderId);
        if (order.getDeliveryPrice() == null) {
            calculateDeliveryCost(orderId);
        }
        order.setProductPrice(paymentApi.productCost(toDto(order)));
        order.setTotalPrice(paymentApi.getTotalCost(toDto(order)));
        return toDto(order);
    }

    @Transactional
    public OrderDto payment(UUID orderId) {
        OrderEntity order = findOrder(orderId);
        if (order.getPaymentId() == null) {
            if (order.getTotalPrice() == null) {
                calculateTotalCost(orderId);
            }
            PaymentDto payment = paymentApi.payment(toDto(order));
            order.setPaymentId(payment.paymentId());
            order.setTotalPrice(payment.totalPayment());
            order.setDeliveryPrice(payment.deliveryTotal());
        }
        order.setState(OrderState.PAID);
        return toDto(order);
    }

    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        OrderEntity order = findOrder(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return toDto(order);
    }

    @Transactional
    public OrderDto delivery(UUID orderId) {
        OrderEntity order = findOrder(orderId);
        order.setState(OrderState.DELIVERED);
        return toDto(order);
    }

    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        OrderEntity order = findOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return toDto(order);
    }

    @Transactional
    public OrderDto complete(UUID orderId) {
        OrderEntity order = findOrder(orderId);
        order.setState(OrderState.COMPLETED);
        return toDto(order);
    }

    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        OrderEntity order = findOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return toDto(order);
    }

    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        if (request == null || request.orderId() == null || request.products() == null || request.products().isEmpty()) {
            throw new InvalidOrderRequestException("Order id and returned products must be filled");
        }
        OrderEntity order = findOrder(request.orderId());
        warehouseApi.acceptReturn(request.products());
        order.setState(OrderState.PRODUCT_RETURNED);
        return toDto(order);
    }

    private OrderEntity findOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(orderId));
    }

    private void validateCreateRequest(CreateNewOrderRequest request) {
        if (request == null || request.shoppingCart() == null || request.shoppingCart().shoppingCartId() == null
                || request.shoppingCart().products() == null || request.shoppingCart().products().isEmpty()
                || request.deliveryAddress() == null || request.username() == null || request.username().isBlank()) {
            throw new InvalidOrderRequestException("Shopping cart, delivery address and username must be filled");
        }
    }

    private OrderDto toDto(OrderEntity order) {
        return new OrderDto(
                order.getOrderId(),
                order.getShoppingCartId(),
                order.getProducts(),
                order.getPaymentId(),
                order.getDeliveryId(),
                order.getState(),
                order.getDeliveryWeight(),
                order.getDeliveryVolume(),
                order.getFragile(),
                order.getTotalPrice(),
                order.getDeliveryPrice(),
                order.getProductPrice()
        );
    }
}
