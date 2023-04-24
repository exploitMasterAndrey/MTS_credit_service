package com.example.creditservice.exception;

public class LoanAlreadyApprovedException extends CreditServiceException {
    public LoanAlreadyApprovedException(String message) {
        super(message, "LOAN_ALREADY_APPROVED");
    }
}
