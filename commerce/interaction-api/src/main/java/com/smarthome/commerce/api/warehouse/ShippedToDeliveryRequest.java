package com.smarthome.commerce.api.warehouse;

import java.util.UUID;

public record ShippedToDeliveryRequest(UUID orderId, UUID deliveryId) {
}
