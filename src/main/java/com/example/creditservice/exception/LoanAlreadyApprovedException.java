package com.example.creditservice.exception;

public class LoanAlreadyApprovedException extends RuntimeException implements ExceptionWrapper {
    private static final String CODE = "LOAN_ALREADY_APPROVED";

    public LoanAlreadyApprovedException(String message) {
        super(message);
    }

    @Override
    public String getCode() {
        return CODE;
    }
}
