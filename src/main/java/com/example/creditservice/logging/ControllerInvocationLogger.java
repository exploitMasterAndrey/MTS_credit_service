package com.example.creditservice.logging;

import com.example.creditservice.controller.CreditController;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class ControllerInvocationLogger {
    private static final Logger logger = LoggerFactory.getLogger(CreditController.class);

    private final ObjectMapper objectMapper;

    @Pointcut("within(com.example.creditservice.controller..*)")
    public void controllerPointCut() {
    }

    @Before("controllerPointCut()")
    @SneakyThrows
    public void logControllerInvocation(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        String methodArgs = Arrays.stream(joinPoint.getArgs()).toList().toString();

        logger.info("Request: " + objectMapper.writeValueAsString(new LoggerMessage(className, methodName, methodArgs)));
    }

    record LoggerMessage(String className, String methodName, String methodArgs) {
    }
}
