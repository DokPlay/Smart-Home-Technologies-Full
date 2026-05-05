package com.smarthome.commerce.api.cart;

import java.util.Map;
import java.util.UUID;

public record ShoppingCartDto(UUID shoppingCartId, Map<UUID, Long> products) {
}
