package com.example.creditservice.exception;

public class OrderImpossibleToDeleteException extends CreditServiceException {
    public OrderImpossibleToDeleteException(String message) {
        super(message, "ORDER_IMPOSSIBLE_TO_DELETE");
    }
}
