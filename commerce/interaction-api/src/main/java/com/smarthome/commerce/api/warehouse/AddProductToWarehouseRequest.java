package com.smarthome.commerce.api.warehouse;

import java.util.UUID;

public record AddProductToWarehouseRequest(UUID productId, Long quantity) {
}
