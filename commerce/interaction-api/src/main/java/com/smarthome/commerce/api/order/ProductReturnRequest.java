package com.smarthome.commerce.api.order;

import java.util.Map;
import java.util.UUID;

public record ProductReturnRequest(UUID orderId, Map<UUID, Long> products) {
}
