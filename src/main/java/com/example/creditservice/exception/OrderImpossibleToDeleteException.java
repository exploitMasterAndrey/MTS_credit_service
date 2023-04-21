package com.example.creditservice.exception;

public class OrderImpossibleToDeleteException extends RuntimeException implements ExceptionWrapper {
    private static final String CODE = "ORDER_IMPOSSIBLE_TO_DELETE";

    public OrderImpossibleToDeleteException(String message) {
        super(message);
    }

    @Override
    public String getCode() {
        return CODE;
    }
}
