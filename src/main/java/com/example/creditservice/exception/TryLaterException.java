package com.example.creditservice.exception;

public class TryLaterException extends RuntimeException implements ExceptionWrapper {
    private static final String CODE = "TRY_LATER";

    public TryLaterException(String message) {
        super(message);
    }

    @Override
    public String getCode() {
        return CODE;
    }
}
