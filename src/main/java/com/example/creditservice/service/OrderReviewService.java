package com.example.creditservice.service;

import com.example.creditservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderReviewService {
    private final OrderRepository orderRepository;

    @Async
    @Scheduled(fixedRate = 120000, initialDelay = 60000)
    protected void makeADecisionOnOrder() {
        orderRepository.updateStatusWhereStatusInProgress();
    }

}
