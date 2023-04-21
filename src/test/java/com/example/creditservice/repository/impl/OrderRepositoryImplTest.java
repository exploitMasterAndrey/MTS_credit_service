package com.example.creditservice.repository.impl;

import com.example.creditservice.model.Order;
import com.example.creditservice.model.Status;
import com.example.creditservice.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Testcontainers
@SpringBootTest
class OrderRepositoryImplTest {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    void save() {
        Integer inserted = orderRepository.save(new Order(
                1L,
                "36a15a00-148f-4168-940f-f71dbee52d4f",
                1L,
                3L,
                0.80,
                Status.IN_PROGRESS,
                Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now())
        ));
        Assertions.assertEquals(1, inserted);
    }

    @Test
    void findOrdersByUserIdAndTariffId() {
        List<Order> expected = List.of(new Order(
                1L,
                "41966075-f753-41ad-a3cb-4aa26d236c8d",
                1L,
                2L,
                0.81,
                Status.REFUSED,
                Timestamp.valueOf("2022-04-20 07:05:54.094000"),
                Timestamp.valueOf("2022-05-20 08:06:00.094000")
        ));

        Assertions.assertAll(() ->
                Assertions.assertArrayEquals(expected.toArray(), orderRepository.findOrdersByUserIdAndTariffId(1L, 2L).toArray()));
                Assertions.assertEquals(Collections.emptyList(), orderRepository.findOrdersByUserIdAndTariffId(1L, 4L));
                Assertions.assertEquals(Collections.emptyList(), orderRepository.findOrdersByUserIdAndTariffId(10L, 1L));
                Assertions.assertEquals(Collections.emptyList(), orderRepository.findOrdersByUserIdAndTariffId(11L, 11L));
    }

    @Test
    void findOrderStatusByOrderId() {
        Assertions.assertAll(() -> {
            Assertions.assertEquals(Status.REFUSED, orderRepository.findOrderStatusByOrderId("41966075-f753-41ad-a3cb-4aa26d236c8d").get());
            Assertions.assertTrue(orderRepository.findOrderStatusByOrderId("41966075-f753-45ad-a3cc-4aa66d236c8d").isEmpty());
        });
    }

    @Test
    void updateStatusWhereStatusInProgress() {
        Assertions.assertAll(() -> {
            Assertions.assertEquals(1, orderRepository.updateStatusWhereStatusInProgress());
            Assertions.assertEquals(0 ,orderRepository.updateStatusWhereStatusInProgress());
        });
    }

    @Test
    void findOrderByUserIdAndOrderId() {
        Order expected = new Order(
                3L,
                "7bce0119-db79-4163-af89-c25eff4c9aad",
                2L,
                2L,
                0.61,
                Status.APPROVED,
                Timestamp.valueOf("2023-04-08 07:20:00.094000"),
                Timestamp.valueOf("2023-04-12 08:30:00.094000")
        );

        Assertions.assertAll(() -> {
            Assertions.assertEquals(expected, orderRepository.findOrderByUserIdAndOrderId(2L, "7bce0119-db79-4163-af89-c25eff4c9aad").get());
            Assertions.assertTrue(orderRepository.findOrderByUserIdAndOrderId(10L, "7bce0119-db79-4163-af89-c25eff4c9aad").isEmpty());
            Assertions.assertTrue(orderRepository.findOrderByUserIdAndOrderId(2L, "7bce2119-db89-4163-af90-c25eff4c9aad").isEmpty());
            Assertions.assertTrue(orderRepository.findOrderByUserIdAndOrderId(10L, "7bce0889-db79-4263-af89-c25eff4c9aad").isEmpty());
        });
    }

    @Test
    void deleteOrderByOrderId() {
        Assertions.assertAll(() -> {
            Assertions.assertEquals(1, orderRepository.deleteOrderByOrderId("5c7c2809-f792-486b-bca1-34b259fa13eb"));
            Assertions.assertEquals(0, orderRepository.deleteOrderByOrderId("5c7c2809-f792-486b-bca1-34b259fa13eb"));
        });
    }
}