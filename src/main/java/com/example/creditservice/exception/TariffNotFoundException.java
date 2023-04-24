package com.example.creditservice.exception;

public class TariffNotFoundException extends CreditServiceException {
    public TariffNotFoundException(String message) {
        super(message, "TARIFF_NOT_FOUND");
    }
}
