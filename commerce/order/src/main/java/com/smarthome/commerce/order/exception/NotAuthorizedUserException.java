package com.smarthome.commerce.order.exception;

public class NotAuthorizedUserException extends RuntimeException {
    public NotAuthorizedUserException() {
        super("Username must not be blank");
    }
}
