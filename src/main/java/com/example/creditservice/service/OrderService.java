package com.example.creditservice.service;

import com.example.creditservice.model.Status;

public interface OrderService {
    String createOrder(Long tariffId, Long userId);
    Status getOrderStatus(String orderId);
    void deleteOrderByUserIdAndOrderId(Long userId, String orderId);
    void makeADecisionOnOrder();
}
