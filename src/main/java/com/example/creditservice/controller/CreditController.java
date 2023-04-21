package com.example.creditservice.controller;

import com.example.creditservice.model.Status;
import com.example.creditservice.model.Tariff;
import com.example.creditservice.service.OrderService;
import com.example.creditservice.service.TariffService;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan-service")
public class CreditController {
    private final TariffService tariffService;
    private final OrderService orderService;

    @HystrixCommand(
            fallbackMethod = "timeoutFallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
            }
    )
    @GetMapping("/getTariffs")
//    @SneakyThrows
    public ResponseEntity<?> getTariffs() {
//        Thread.sleep(1500);
        List<Tariff> allTariffs = tariffService.getAllTariffs();
        return ResponseEntity.ok(new TariffResponse(allTariffs));
    }

//    @HystrixCommand(
//            fallbackMethod = "timeoutFallback",
//            commandProperties = {
//                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
//            }
//    )
    @PostMapping("/order")
//    @SneakyThrows
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest orderRequest) {
//        Thread.sleep(1500);
        String orderId = orderService.createOrder(orderRequest.tariffId, orderRequest.userId);
        return ResponseEntity.ok(new CreateOrderResponse(orderId));
    }

//    @HystrixCommand(
//            fallbackMethod = "timeoutFallback",
//            commandProperties = {
//                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
//            }
//    )
    @GetMapping("/getStatusOrder")
    public ResponseEntity<?> getOrderStatus(@RequestParam String orderId) {
        Status orderStatus = orderService.getOrderStatus(orderId);
        return ResponseEntity.ok(new GetOrderStatusResponse(orderStatus));
    }

//    @HystrixCommand(
//            fallbackMethod = "timeoutFallback",
//            commandProperties = {
//                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
//            }
//    )
    @DeleteMapping("/deleteOrder")
    public ResponseEntity<?> deleteOrder(@RequestBody DeleteOrderRequest deleteOrderRequest) {
        orderService.deleteOrderByUserIdAndOrderId(deleteOrderRequest.userId, deleteOrderRequest.orderId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> timeoutFallback(){
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(new TimeoutResponse("TIMEOUT", "Что-то пошло не так. Время запроса превышено"));
    }

    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    @JsonTypeName("error")
    record TimeoutResponse(String code, String message){}

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
