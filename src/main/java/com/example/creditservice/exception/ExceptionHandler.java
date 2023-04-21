package com.example.creditservice.exception;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler({TariffNotFoundException.class, LoanConsiderationException.class, LoanAlreadyApprovedException.class, TryLaterException.class, OrderNotFoundException.class, OrderImpossibleToDeleteException.class})
    public ResponseEntity<?> handleException(ExceptionWrapper ex) {
        return ResponseEntity
                .badRequest()
                .body(new ExceptionResponse(ex.getCode(), ex.getMessage()));
    }

    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    @JsonTypeName("error")
    record ExceptionResponse(String code, String message) {
    }
}
