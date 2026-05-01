package com.smarthome.commerce.warehouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidWarehouseProductQuantityException extends RuntimeException {

    public InvalidWarehouseProductQuantityException(String message) {
        super(message);
    }
}
