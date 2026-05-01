package com.smarthome.commerce.cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class WarehouseServiceUnavailableException extends RuntimeException {

    public static final String MESSAGE = "Warehouse service is temporarily unavailable. Please try again later.";

    public WarehouseServiceUnavailableException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
