package com.example.creditservice.exception;

public class LoanConsiderationException extends CreditServiceException {
    public LoanConsiderationException(String message) {
        super(message, "LOAN_CONSIDERATION");
    }
}
