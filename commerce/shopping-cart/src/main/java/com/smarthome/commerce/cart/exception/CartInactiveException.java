package com.smarthome.commerce.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CartInactiveException extends RuntimeException {

    public CartInactiveException(String username) {
        super("Shopping cart is inactive for user: " + username);
    }
}
