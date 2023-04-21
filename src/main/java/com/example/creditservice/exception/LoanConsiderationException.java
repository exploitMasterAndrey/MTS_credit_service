package com.example.creditservice.exception;

public class LoanConsiderationException extends RuntimeException implements ExceptionWrapper {
    private static final String CODE = "LOAN_CONSIDERATION";

    public LoanConsiderationException(String message) {
        super(message);
    }

    @Override
    public String getCode() {
        return CODE;
    }
}
