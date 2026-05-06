package com.smarthome.commerce.payment.controller;

import com.smarthome.commerce.payment.exception.NoPaymentFoundException;
import com.smarthome.commerce.payment.exception.NotEnoughInfoInOrderToCalculateException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler(NotEnoughInfoInOrderToCalculateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleCalculationError(NotEnoughInfoInOrderToCalculateException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(NoPaymentFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlePaymentNotFound(NoPaymentFoundException exception) {
        return exception.getMessage();
    }
}
