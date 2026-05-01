package com.smarthome.commerce.cart.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.smarthome.commerce.api.cart.ChangeProductQuantityRequest;
import com.smarthome.commerce.api.cart.ShoppingCartApi;
import com.smarthome.commerce.api.cart.ShoppingCartDto;
import com.smarthome.commerce.cart.service.CartService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartController implements ShoppingCartApi {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        return cartService.getShoppingCart(username);
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> products) {
        return cartService.addProductToShoppingCart(username, products);
    }

    @Override
    public void deactivateCurrentShoppingCart(String username) {
        cartService.deactivateCurrentShoppingCart(username);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> products) {
        return cartService.removeFromShoppingCart(username, products);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        return cartService.changeProductQuantity(username, request);
    }
}
