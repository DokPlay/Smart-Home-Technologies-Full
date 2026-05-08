package com.smarthome.commerce.delivery.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import com.smarthome.commerce.api.delivery.DeliveryDto;
import com.smarthome.commerce.api.delivery.DeliveryState;
import com.smarthome.commerce.api.order.OrderApi;
import com.smarthome.commerce.api.order.OrderDto;
import com.smarthome.commerce.api.warehouse.ShippedToDeliveryRequest;
import com.smarthome.commerce.api.warehouse.WarehouseApi;
import com.smarthome.commerce.delivery.exception.InvalidDeliveryRequestException;
import com.smarthome.commerce.delivery.exception.NoDeliveryFoundException;
import com.smarthome.commerce.delivery.model.AddressEmbeddable;
import com.smarthome.commerce.delivery.model.DeliveryEntity;
import com.smarthome.commerce.delivery.repository.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryService {

    private static final BigDecimal BASE_COST = BigDecimal.valueOf(5.0);
    private static final BigDecimal ADDRESS_2_MULTIPLIER = BigDecimal.valueOf(2.0);
    private static final BigDecimal SAME_WAREHOUSE_MULTIPLIER = BigDecimal.valueOf(1.0);
    private static final BigDecimal EXTRA_RATE = BigDecimal.valueOf(0.2);
    private static final BigDecimal WEIGHT_RATE = BigDecimal.valueOf(0.3);
    private static final BigDecimal VOLUME_RATE = BigDecimal.valueOf(0.2);

    private final DeliveryRepository deliveryRepository;
    private final OrderApi orderApi;
    private final WarehouseApi warehouseApi;

    public DeliveryService(DeliveryRepository deliveryRepository, OrderApi orderApi, WarehouseApi warehouseApi) {
        this.deliveryRepository = deliveryRepository;
        this.orderApi = orderApi;
        this.warehouseApi = warehouseApi;
    }

    @Transactional
    public DeliveryDto planDelivery(DeliveryDto delivery) {
        validateDelivery(delivery);
        UUID deliveryId = delivery.deliveryId() == null ? UUID.randomUUID() : delivery.deliveryId();
        DeliveryState state = delivery.deliveryState() == null ? DeliveryState.CREATED : delivery.deliveryState();
        DeliveryEntity entity = deliveryRepository.findById(deliveryId)
                .or(() -> deliveryRepository.findFirstByOrderIdOrderByDeliveryId(delivery.orderId()))
                .orElseGet(() -> new DeliveryEntity(
                        deliveryId,
                        new AddressEmbeddable(delivery.fromAddress()),
                        new AddressEmbeddable(delivery.toAddress()),
                        delivery.orderId(),
                        state
                ));
        entity.setFromAddress(new AddressEmbeddable(delivery.fromAddress()));
        entity.setToAddress(new AddressEmbeddable(delivery.toAddress()));
        entity.setOrderId(delivery.orderId());
        entity.setDeliveryState(state);
        entity = deliveryRepository.save(entity);
        return toDto(entity);
    }

    @Transactional(readOnly = true)
    public BigDecimal deliveryCost(OrderDto order) {
        if (order == null || order.deliveryId() == null) {
            throw new NoDeliveryFoundException(null);
        }
        DeliveryEntity delivery = findDelivery(order.deliveryId());
        BigDecimal total = BASE_COST.multiply(warehouseMultiplier(delivery)).add(BASE_COST);

        if (Boolean.TRUE.equals(order.fragile())) {
            total = total.add(total.multiply(EXTRA_RATE));
        }
        total = total.add(BigDecimal.valueOf(value(order.deliveryWeight())).multiply(WEIGHT_RATE));
        total = total.add(BigDecimal.valueOf(value(order.deliveryVolume())).multiply(VOLUME_RATE));

        if (!sameStreet(delivery)) {
            total = total.add(total.multiply(EXTRA_RATE));
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public void deliveryPicked(UUID orderId) {
        DeliveryEntity delivery = findByOrderId(orderId);
        orderApi.assembly(orderId);
        warehouseApi.shippedToDelivery(new ShippedToDeliveryRequest(orderId, delivery.getDeliveryId()));
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
    }

    @Transactional
    public void deliverySuccessful(UUID orderId) {
        DeliveryEntity delivery = findByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        orderApi.delivery(orderId);
    }

    @Transactional
    public void deliveryFailed(UUID orderId) {
        DeliveryEntity delivery = findByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        orderApi.deliveryFailed(orderId);
    }

    private DeliveryEntity findDelivery(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException(deliveryId));
    }

    private DeliveryEntity findByOrderId(UUID orderId) {
        return deliveryRepository.findFirstByOrderIdOrderByDeliveryId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException(orderId));
    }

    private void validateDelivery(DeliveryDto delivery) {
        if (delivery == null || delivery.fromAddress() == null || delivery.toAddress() == null
                || delivery.orderId() == null) {
            throw new InvalidDeliveryRequestException("Delivery addresses and order id must be filled");
        }
    }

    private BigDecimal warehouseMultiplier(DeliveryEntity delivery) {
        String text = delivery.getFromAddress().toDto().toString();
        return text.contains("ADDRESS_2") ? ADDRESS_2_MULTIPLIER : SAME_WAREHOUSE_MULTIPLIER;
    }

    private boolean sameStreet(DeliveryEntity delivery) {
        String fromStreet = delivery.getFromAddress().getStreet();
        String toStreet = delivery.getToAddress().getStreet();
        return fromStreet != null && fromStreet.equalsIgnoreCase(toStreet);
    }

    private double value(Double number) {
        return number == null ? 0.0 : number;
    }

    private DeliveryDto toDto(DeliveryEntity delivery) {
        return new DeliveryDto(
                delivery.getDeliveryId(),
                delivery.getFromAddress().toDto(),
                delivery.getToAddress().toDto(),
                delivery.getOrderId(),
                delivery.getDeliveryState()
        );
    }
}
