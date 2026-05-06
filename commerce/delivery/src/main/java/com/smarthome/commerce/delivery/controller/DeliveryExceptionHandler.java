package com.smarthome.commerce.delivery.controller;

import com.smarthome.commerce.delivery.exception.InvalidDeliveryRequestException;
import com.smarthome.commerce.delivery.exception.NoDeliveryFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DeliveryExceptionHandler {

    @ExceptionHandler(InvalidDeliveryRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidRequest(InvalidDeliveryRequestException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(NoDeliveryFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleDeliveryNotFound(NoDeliveryFoundException exception) {
        return exception.getMessage();
    }
}
