package com.example.creditservice.schedulers;

import com.example.creditservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderScheduler {
    private final OrderService orderService;

    @Async
    @Scheduled(fixedRate = 120000, initialDelay = 60000)
    public void updateOrdersStatus(){
        orderService.makeADecisionOnOrder();
    }
}
