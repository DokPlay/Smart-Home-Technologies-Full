package com.smarthome.commerce.delivery.exception;

import java.util.UUID;

public class NoDeliveryFoundException extends RuntimeException {
    public NoDeliveryFoundException(UUID id) {
        super("Delivery not found: " + id);
    }
}
