package com.smarthome.commerce.api.delivery;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class DeliveryServiceUnavailableException extends RuntimeException {

    public static final String MESSAGE = "Delivery service is temporarily unavailable. Please try again later.";

    public DeliveryServiceUnavailableException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
