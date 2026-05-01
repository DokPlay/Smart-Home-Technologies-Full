package com.smarthome.commerce.api.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.smarthome.commerce.api.cart.CartItemDto;

public record OrderDto(UUID id, String username, List<CartItemDto> items, BigDecimal total, String status) {
}
