package com.example.creditservice.exception;

public class OrderNotFoundException extends RuntimeException implements ExceptionWrapper {
    private static final String CODE = "ORDER_NOT_FOUND";

    public OrderNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getCode() {
        return CODE;
    }
}
