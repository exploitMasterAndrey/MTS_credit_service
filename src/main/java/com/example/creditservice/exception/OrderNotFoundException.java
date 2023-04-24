package com.example.creditservice.exception;

public class OrderNotFoundException extends CreditServiceException {
    public OrderNotFoundException(String message) {
        super(message, "ORDER_NOT_FOUND");
    }
}
