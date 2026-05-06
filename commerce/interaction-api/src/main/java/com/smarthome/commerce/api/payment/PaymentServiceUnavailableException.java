package com.smarthome.commerce.api.payment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class PaymentServiceUnavailableException extends RuntimeException {

    public static final String MESSAGE = "Payment service is temporarily unavailable. Please try again later.";

    public PaymentServiceUnavailableException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
