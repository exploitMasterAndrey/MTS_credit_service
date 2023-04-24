package com.example.creditservice.controller.impl;

import com.example.creditservice.controller.CreditController;
import com.example.creditservice.model.Status;
import com.example.creditservice.model.Tariff;
import com.example.creditservice.service.impl.OrderServiceImpl;
import com.example.creditservice.service.impl.TariffServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan-service")
public class CreditControllerImpl implements CreditController {
    private final TariffServiceImpl tariffService;
    private final OrderServiceImpl orderService;

    @GetMapping("/getTariffs")
    public ResponseEntity<TariffResponse> getTariffs() {
        List<Tariff> allTariffs = tariffService.getAllTariffs();
        return ResponseEntity.ok(new TariffResponse(allTariffs));
    }

    @PostMapping("/order")
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest orderRequest) {
        String orderId = orderService.createOrder(orderRequest.tariffId(), orderRequest.userId());
        return ResponseEntity.ok(new CreateOrderResponse(orderId));
    }

    @GetMapping("/getStatusOrder")
    public ResponseEntity<GetOrderStatusResponse> getOrderStatus(@RequestParam String orderId) {
        Status orderStatus = orderService.getOrderStatus(orderId);
        return ResponseEntity.ok(new GetOrderStatusResponse(orderStatus));
    }

    @DeleteMapping("/deleteOrder")
    public ResponseEntity<?> deleteOrder(@RequestBody DeleteOrderRequest deleteOrderRequest) {
        orderService.deleteOrderByUserIdAndOrderId(deleteOrderRequest.userId(), deleteOrderRequest.orderId());
        return ResponseEntity.ok().build();
    }
}
