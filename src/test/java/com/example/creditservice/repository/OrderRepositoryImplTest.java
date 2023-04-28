package com.example.creditservice.repository;

import com.example.creditservice.model.Order;
import com.example.creditservice.model.Status;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Testcontainers
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderRepositoryImplTest {
    @Container
    private static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.consumer.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.producer.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Autowired
    private OrderRepository orderRepository;

    private static List<Order> orders;

    @BeforeAll
    public static void init() {
        kafkaContainer.start();
        orders = List.of(
                new Order(
                        1L,
                        "41966075-f753-41ad-a3cb-4aa26d236c8d",
                        1L,
                        2L,
                        0.81,
                        Status.REFUSED,
                        Timestamp.valueOf("2022-04-20 07:05:54.094000"),
                        Timestamp.valueOf("2022-05-20 08:06:00.094000"),
                        1
                ),
                new Order(
                        2L,
                        "5c7c2809-f792-486b-bca1-34b259fa13eb",
                        2L,
                        1L,
                        0.59,
                        Status.IN_PROGRESS,
                        Timestamp.valueOf("2023-04-21 11:00:00.094000"),
                        Timestamp.valueOf("2023-04-21 11:00:00.094000"),
                        1
                ),
                new Order(
                        3L,
                        "7bce0119-db79-4163-af89-c25eff4c9aad",
                        2L,
                        2L,
                        0.61,
                        Status.APPROVED,
                        Timestamp.valueOf("2023-04-08 07:20:00.094000"),
                        Timestamp.valueOf("2023-04-12 08:30:00.094000"),
                        0
                ),
                new Order(
                        4L,
                        "24a63177-a8b1-4546-a7c6-5279d5b35bf3",
                        2L,
                        3L,
                        0.60,
                        Status.REFUSED,
                        Timestamp.valueOf("2023-04-15 18:01:20.094000"),
                        Timestamp.valueOf("2023-04-17 10:15:20.094000"),
                        1
                ),
                new Order(
                        1L,
                        "36a15a00-148f-4168-940f-f71dbee52d4f",
                        1L,
                        3L,
                        0.80,
                        Status.IN_PROGRESS,
                        Timestamp.valueOf(LocalDateTime.now()),
                        Timestamp.valueOf(LocalDateTime.now()),
                        1
                )
        );
    }

    @Test
    void save() {
        Integer inserted = orderRepository.save(orders.get(4));
        Assertions.assertEquals(1, inserted);
    }

    @Test
    void findOrdersByUserIdAndTariffId() {
        List<Order> expected = List.of(orders.get(0));
        long nonExistingTariffId = 4, nonExistingUserId = 10;

        Assertions.assertAll(() ->
                {
                    Assertions.assertArrayEquals(expected.toArray(), orderRepository.findOrdersByUserIdAndTariffId(expected.get(0).getUserId(), expected.get(0).getTariffId()).toArray());
                    Assertions.assertEquals(Collections.emptyList(), orderRepository.findOrdersByUserIdAndTariffId(expected.get(0).getUserId(), nonExistingTariffId));
                    Assertions.assertEquals(Collections.emptyList(), orderRepository.findOrdersByUserIdAndTariffId(nonExistingUserId, expected.get(0).getTariffId()));
                    Assertions.assertEquals(Collections.emptyList(), orderRepository.findOrdersByUserIdAndTariffId(nonExistingUserId, nonExistingTariffId));
                }
        );
    }

    @Test
    void findOrderStatusByOrderId() {
        String nonExistingOrderId = "41966075-f753-45ad-a3cc-4aa66d236c8d";
        Assertions.assertAll(() -> {
            Assertions.assertEquals(Status.REFUSED, orderRepository.findOrderStatusByOrderId(orders.get(0).getOrderId()).get());
            Assertions.assertTrue(orderRepository.findOrderStatusByOrderId(nonExistingOrderId).isEmpty());
        });
    }

    @org.junit.jupiter.api.Order(1)
    @Test
    void findOrdersWhereStatus() {
        List<Order> expected = List.of(orders.get(0), orders.get(3));
        Assertions.assertArrayEquals(expected.toArray(), orderRepository.findOrdersWhereStatus(Status.REFUSED).toArray());
    }

    @Test
    void findOrderByUserIdAndOrderId() {
        Order expected = orders.get(2);
        long nonExistingUserId = 10;
        String nonExistingOrderId = "7bce2119-db89-4163-af90-c25eff4c9aad";
        Assertions.assertAll(() -> {
            Assertions.assertEquals(expected, orderRepository.findOrderByUserIdAndOrderId(expected.getUserId(), expected.getOrderId()).get());
            Assertions.assertTrue(orderRepository.findOrderByUserIdAndOrderId(nonExistingUserId, expected.getOrderId()).isEmpty());
            Assertions.assertTrue(orderRepository.findOrderByUserIdAndOrderId(expected.getUserId(), nonExistingOrderId).isEmpty());
            Assertions.assertTrue(orderRepository.findOrderByUserIdAndOrderId(nonExistingUserId, nonExistingOrderId).isEmpty());
        });
    }

    @Test
    void findOrdersToSend() {
        List<Order> expected = List.of(orders.get(2));
        Assertions.assertArrayEquals(expected.toArray(), orderRepository.findOrdersToSend().toArray());
    }

    @Test
    void updateOrderStatus() {
        List<Order> expected = List.of(orders.get(1), orders.get(4));
        orderRepository.updateOrderStatus(expected);
        Assertions.assertEquals(2, orderRepository.updateOrderStatus(expected).length);
    }

    @Test
    void updateOrderSending() {
        Assertions.assertEquals(1, orderRepository.updateOrderSending(4L));
    }

    @Test
    void deleteOrderByOrderId() {
        String orderIdToDelete = orders.get(1).getOrderId();
        Assertions.assertAll(() -> {
            Assertions.assertEquals(1, orderRepository.deleteOrderByOrderId(orderIdToDelete));
            Assertions.assertEquals(0, orderRepository.deleteOrderByOrderId(orderIdToDelete));
        });
    }
}