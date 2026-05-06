package com.smarthome.commerce.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ShoppingStoreServiceUnavailableException extends RuntimeException {

    public static final String MESSAGE = "Shopping store service is temporarily unavailable. Please try again later.";

    public ShoppingStoreServiceUnavailableException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
