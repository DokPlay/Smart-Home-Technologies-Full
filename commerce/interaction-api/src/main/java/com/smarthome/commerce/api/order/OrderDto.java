package com.smarthome.commerce.api.order;

import com.smarthome.commerce.api.cart.CartItemDto;

import java.math.BigDecimal;
import java.util.List;

public record OrderDto(Long id, String username, List<CartItemDto> items, BigDecimal total, String status) {
}
