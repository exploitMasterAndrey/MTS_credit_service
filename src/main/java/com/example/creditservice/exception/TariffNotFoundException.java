package com.example.creditservice.exception;

public class TariffNotFoundException extends RuntimeException implements ExceptionWrapper {
    private static final String CODE = "TARIFF_NOT_FOUND";

    public TariffNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getCode() {
        return CODE;
    }
}
