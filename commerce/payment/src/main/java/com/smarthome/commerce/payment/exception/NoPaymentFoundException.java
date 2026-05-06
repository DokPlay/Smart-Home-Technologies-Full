package com.smarthome.commerce.payment.exception;

import java.util.UUID;

public class NoPaymentFoundException extends RuntimeException {
    public NoPaymentFoundException(UUID paymentId) {
        super("Payment not found: " + paymentId);
    }
}
