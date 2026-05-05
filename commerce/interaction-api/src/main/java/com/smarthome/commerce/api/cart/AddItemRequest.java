package com.smarthome.commerce.api.cart;

import java.util.UUID;

public record AddItemRequest(UUID productId, Long quantity) {
}
