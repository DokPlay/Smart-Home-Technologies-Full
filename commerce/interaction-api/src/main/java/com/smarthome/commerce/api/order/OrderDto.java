package com.smarthome.commerce.api.order;

import java.math.BigDecimal;
import java.util.List;

import com.smarthome.commerce.api.cart.CartItemDto;

public record OrderDto(Long id, String username, List<CartItemDto> items, BigDecimal total, String status) {
}
