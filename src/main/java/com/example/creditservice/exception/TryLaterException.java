package com.example.creditservice.exception;

public class TryLaterException extends CreditServiceException {
    public TryLaterException(String message) {
        super(message, "TRY_LATER");
    }
}
