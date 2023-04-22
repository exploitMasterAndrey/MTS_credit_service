package com.example.creditservice.controller;

import com.example.creditservice.exception.ExceptionHandler;
import com.example.creditservice.model.Status;
import com.example.creditservice.model.Tariff;
import com.example.creditservice.service.OrderService;
import com.example.creditservice.service.TariffService;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan-service")
public class CreditController {
    private final TariffService tariffService;
    private final OrderService orderService;

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
    @HystrixCommand(
            fallbackMethod = "getTariffsFallback",
            ignoreExceptions = { RuntimeException.class },
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
            }
    )
    @GetMapping("/getTariffs")
    public ResponseEntity<?> getTariffs() {
        List<Tariff> allTariffs = tariffService.getAllTariffs();
        return ResponseEntity.ok(new TariffResponse(allTariffs));
    }

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
    @HystrixCommand(
            fallbackMethod = "createOrderFallback",
            ignoreExceptions = { RuntimeException.class },
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
            }
    )
    @PostMapping("/order")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest orderRequest) {
        String orderId = orderService.createOrder(orderRequest.tariffId, orderRequest.userId);
        return ResponseEntity.ok(new CreateOrderResponse(orderId));
    }

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
    @HystrixCommand(
            fallbackMethod = "getOrderStatusFallback",
            ignoreExceptions = { RuntimeException.class },
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
            }
    )
    @GetMapping("/getStatusOrder")
    public ResponseEntity<?> getOrderStatus(@RequestParam String orderId) {
        Status orderStatus = orderService.getOrderStatus(orderId);
        return ResponseEntity.ok(new GetOrderStatusResponse(orderStatus));
    }

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
    @HystrixCommand(
            fallbackMethod = "deleteOrderFallback",
            ignoreExceptions = { RuntimeException.class },
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
            }
    )
    @DeleteMapping("/deleteOrder")
    public ResponseEntity<?> deleteOrder(@RequestBody DeleteOrderRequest deleteOrderRequest) {
        orderService.deleteOrderByUserIdAndOrderId(deleteOrderRequest.userId, deleteOrderRequest.orderId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> getTariffsFallback() {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(new ExceptionHandler.ExceptionResponse("TIMEOUT", "Что-то пошло не так при получении тарифов. Время запроса превышено"));
    }

    public ResponseEntity<?> createOrderFallback(@RequestBody CreateOrderRequest orderRequest){
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(new ExceptionHandler.ExceptionResponse("TIMEOUT", "Что-то пошло не так при создании заявки. Время запроса превышено"));
    }

    public ResponseEntity<?> getOrderStatusFallback(@RequestParam String orderId) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(new ExceptionHandler.ExceptionResponse("TIMEOUT", "Что-то пошло не так при получении статуса заявки. Время запроса превышено"));
    }

    public ResponseEntity<?> deleteOrderFallback(@RequestBody DeleteOrderRequest deleteOrderRequest) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(new ExceptionHandler.ExceptionResponse("TIMEOUT", "Что-то пошло не так при удалении заявки. Время запроса превышено"));
    }

//    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
//    @JsonTypeName("error")
//    record TimeoutResponse(String code, String message){}

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
