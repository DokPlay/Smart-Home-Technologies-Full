package com.smarthome.commerce.order.controller;

import com.smarthome.commerce.order.exception.InvalidOrderRequestException;
import com.smarthome.commerce.order.exception.NoOrderFoundException;
import com.smarthome.commerce.order.exception.NotAuthorizedUserException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleUnauthorized(NotAuthorizedUserException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(InvalidOrderRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidRequest(InvalidOrderRequestException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(NoOrderFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleOrderNotFound(NoOrderFoundException exception) {
        return exception.getMessage();
    }
}
