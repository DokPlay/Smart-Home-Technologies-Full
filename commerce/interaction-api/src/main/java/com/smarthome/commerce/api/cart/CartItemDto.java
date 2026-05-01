package com.smarthome.commerce.api.cart;

import java.util.UUID;

public record CartItemDto(UUID productId, Long quantity) {
}
