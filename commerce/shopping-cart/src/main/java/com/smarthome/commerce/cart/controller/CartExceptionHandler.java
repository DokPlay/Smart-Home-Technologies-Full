package com.smarthome.commerce.cart.controller;

import com.smarthome.commerce.cart.exception.WarehouseServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CartExceptionHandler {

    @ExceptionHandler(WarehouseServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleWarehouseUnavailable(WarehouseServiceUnavailableException exception) {
        return new ErrorResponse("WAREHOUSE_UNAVAILABLE", exception.getMessage());
    }
}
