package com.smarthome.commerce.api.store;

import java.util.UUID;

public record SetProductQuantityStateRequest(UUID productId, QuantityState quantityState) {
}
