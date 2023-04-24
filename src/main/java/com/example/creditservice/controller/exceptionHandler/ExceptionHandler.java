package com.example.creditservice.controller.exceptionHandler;

import com.example.creditservice.exception.CreditServiceException;
import com.example.creditservice.exception.ExceptionWrapper;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(CreditServiceException.class)
    public ResponseEntity<?> handleException(ExceptionWrapper ex) {
        return ResponseEntity
                .badRequest()
                .body(new ExceptionResponse(ex.getCode(), ex.getMessage()));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(HystrixRuntimeException.class)
    public ResponseEntity<?> handleTimeoutException() {
        return ResponseEntity
                .status(HttpStatus.REQUEST_TIMEOUT)
                .body(new ExceptionResponse("TIMEOUT", "Что-то пошло не так. Время запроса превышено"));
    }

    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    @JsonTypeName("error")
    public record ExceptionResponse(String code, String message) {
    }
}
