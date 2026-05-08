package com.smarthome.commerce.api.delivery;

import java.util.UUID;

import com.smarthome.commerce.api.warehouse.AddressDto;

public record DeliveryDto(
        UUID deliveryId,
        AddressDto fromAddress,
        AddressDto toAddress,
        UUID orderId,
        DeliveryState deliveryState
) {
}
