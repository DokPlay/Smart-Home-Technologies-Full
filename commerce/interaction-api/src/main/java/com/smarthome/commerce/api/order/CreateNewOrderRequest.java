package com.smarthome.commerce.api.order;

import com.smarthome.commerce.api.cart.ShoppingCartDto;
import com.smarthome.commerce.api.warehouse.AddressDto;

public record CreateNewOrderRequest(ShoppingCartDto shoppingCart, AddressDto deliveryAddress, String username) {

    public CreateNewOrderRequest(ShoppingCartDto shoppingCart, AddressDto deliveryAddress) {
        this(shoppingCart, deliveryAddress, null);
    }
}
