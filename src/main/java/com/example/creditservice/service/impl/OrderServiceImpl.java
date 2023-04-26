package com.example.creditservice.service.impl;

import com.example.creditservice.exception.*;
import com.example.creditservice.model.Order;
import com.example.creditservice.model.Status;
import com.example.creditservice.repository.OrderRepository;
import com.example.creditservice.repository.TariffRepository;
import com.example.creditservice.service.OrderService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    @Value("${topic-name}")
    private String topicName;
    private final KafkaTemplate<String, Order> kafkaTemplate;
    private final OrderRepository orderRepository;
    private final TariffRepository tariffRepository;

    @HystrixCommand(
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
            }
    )
    @Override
    public String createOrder(Long tariffId, Long userId) {
        if (tariffRepository.findTariffById(tariffId).isEmpty()) throw new TariffNotFoundException("Тариф не найден");
        List<Order> ordersByUserId = orderRepository.findOrdersByUserIdAndTariffId(userId, tariffId);
        ordersByUserId.forEach(order -> {
            switch (order.getStatus()) {
                case IN_PROGRESS -> throw new LoanConsiderationException("Заявка в процессе обработки");
                case APPROVED -> throw new LoanAlreadyApprovedException("Заявка уже была одобрена");
                case REFUSED -> {
                    Timestamp timeUpdate = order.getTimeUpdate();
                    Timestamp now = Timestamp.valueOf(LocalDateTime.now());
                    if (now.getTime() - timeUpdate.getTime() < 120000) throw new TryLaterException("Попробуйте позже");
                }
            }
        });
        final String orderId = UUID.randomUUID().toString();
        Order order = new Order(
                null,
                orderId,
                userId,
                tariffId,
                Math.floor((Math.random() * 81 + 10)) / 100.0,
                Status.IN_PROGRESS,
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()),
                0
        );
        orderRepository.save(order);
        return orderId;
    }

    @HystrixCommand(
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
            }
    )
    @Override
    public Status getOrderStatus(String orderId) {
        return orderRepository.findOrderStatusByOrderId(orderId).orElseThrow(() -> new OrderNotFoundException("Заявка не найдена"));
    }

    @HystrixCommand(
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
            }
    )
    @Override
    public void deleteOrderByUserIdAndOrderId(Long userId, String orderId) {
        Optional<Order> orderByUserIdAndOrderIdOpt = orderRepository.findOrderByUserIdAndOrderId(userId, orderId);
        Order order = orderByUserIdAndOrderIdOpt.orElseThrow(() -> new OrderNotFoundException("Заявка не найдена"));
        if (order.getStatus().equals(Status.IN_PROGRESS)) {
            orderRepository.deleteOrderByOrderId(orderId);
        } else throw new OrderImpossibleToDeleteException("Невозможно удалить заявку");
    }

    @Override
    public void makeADecisionOnOrder() {
        List<Order> ordersWhereStatus = orderRepository.findOrdersWhereStatus(Status.IN_PROGRESS);
        if (ordersWhereStatus.isEmpty()) return;
        orderRepository.updateOrderStatus(ordersWhereStatus);
        List<Order> ordersToSend = orderRepository.findOrdersToSend();
        ordersToSend.forEach(order -> {
            kafkaTemplate.send(topicName, order);
            orderRepository.updateOrderSending(order.getId());
        });
    }
}
