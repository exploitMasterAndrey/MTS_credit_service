package com.example.creditservice.controller;

import com.example.creditservice.controller.exceptionHandler.ExceptionHandler;
import com.example.creditservice.controller.impl.CreditControllerImpl;
import com.example.creditservice.model.Status;
import com.example.creditservice.model.Tariff;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CreditController {
    @Operation(
            tags = "Получить тарифы",
            summary = "Запрос на получение тарифов",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = TariffResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "408",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionHandler.ExceptionResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    ResponseEntity<?> getTariffs();
    @Operation(
            tags = "Подать заявку",
            summary = "Запрос на подачу заявки",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = CreateOrderResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "400",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionHandler.ExceptionResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "408",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionHandler.ExceptionResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )

    ResponseEntity<?> createOrder(CreditControllerImpl.CreateOrderRequest orderRequest);
    @Operation(
            tags = "Получить статус заявки",
            summary = "Запрос на получение статуса заявки",
            responses = {
                    @ApiResponse(responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = GetOrderStatusResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "400",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionHandler.ExceptionResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "408",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionHandler.ExceptionResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )

    ResponseEntity<?> getOrderStatus(String orderId);

    @Operation(
            tags = "Удалить заявку",
            summary = "Запрос на удаление заявки",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionHandler.ExceptionResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "408",
                            content = @Content(
                                    schema = @Schema(implementation = ExceptionHandler.ExceptionResponse.class),
                                    mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    ResponseEntity<?> deleteOrder(CreditControllerImpl.DeleteOrderRequest deleteOrderRequest);

    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    @JsonTypeName("data")
    record TariffResponse(List<Tariff> tariffs) {
    }

    record CreateOrderRequest(Long userId, Long tariffId) {
    }

    record DeleteOrderRequest(Long userId, String orderId) {
    }

    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    @JsonTypeName("data")
    record CreateOrderResponse(String orderId) {
    }

    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    @JsonTypeName("data")
    record GetOrderStatusResponse(Status orderStatus) {
    }
}
