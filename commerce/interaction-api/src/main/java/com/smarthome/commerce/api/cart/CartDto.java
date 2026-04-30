package com.smarthome.commerce.api.cart;

import java.util.List;

public record CartDto(String username, List<CartItemDto> items, boolean active) {
}
