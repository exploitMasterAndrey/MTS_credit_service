package com.example.creditservice.exception;

public class CreditServiceException extends RuntimeException implements ExceptionWrapper{
    private final String code;
    public CreditServiceException(String message, String code) {
        super(message);
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}