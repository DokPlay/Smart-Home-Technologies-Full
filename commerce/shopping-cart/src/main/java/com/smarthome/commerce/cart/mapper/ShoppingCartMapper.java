package com.smarthome.commerce.cart.mapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.smarthome.commerce.api.cart.ShoppingCartDto;
import com.smarthome.commerce.cart.model.ShoppingCartEntity;
import org.springframework.stereotype.Component;

@Component
public class ShoppingCartMapper {

    public ShoppingCartDto toDto(ShoppingCartEntity cart) {
        Map<UUID, Long> products = new LinkedHashMap<>();
        cart.getItems().forEach(item -> products.put(item.getProductId(), item.getQuantity()));
        return new ShoppingCartDto(cart.getShoppingCartId(), products);
    }
}
