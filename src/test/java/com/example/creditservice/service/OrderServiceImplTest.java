package com.example.creditservice.service;

import com.example.creditservice.exception.*;
import com.example.creditservice.model.Order;
import com.example.creditservice.model.Status;
import com.example.creditservice.model.Tariff;
import com.example.creditservice.repository.OrderRepository;
import com.example.creditservice.repository.TariffRepository;
import com.example.creditservice.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private TariffRepository tariffRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrderThrowsTariffNotFoundException() {
        Mockito.when(tariffRepository.findTariffById(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(TariffNotFoundException.class, () -> orderService.createOrder(1L, 1L));
    }

    @Test
    void createOrderThrowsLoanConsiderationException() {
        Mockito.when(tariffRepository.findTariffById(anyLong())).thenReturn(Optional.of(
                new Tariff(
                        1L,
                        "Потребительский",
                        "7-9%"
                )
        ));
        Mockito.when(orderRepository.findOrdersByUserIdAndTariffId(anyLong(), anyLong())).thenReturn(List.of(
                new Order(
                        1L,
                        UUID.randomUUID().toString(),
                        1L,
                        1L,
                        0.65,
                        Status.IN_PROGRESS,
                        Timestamp.valueOf(LocalDateTime.now()),
                        Timestamp.valueOf(LocalDateTime.now()),
                        0
                )
        ));
        Assertions.assertThrows(LoanConsiderationException.class, () -> orderService.createOrder(1L, 1L));
    }

    @Test
    void createOrderThrowsLoanAlreadyApprovedException() {
        Mockito.when(tariffRepository.findTariffById(anyLong())).thenReturn(Optional.of(
                new Tariff(
                        1L,
                        "Потребительский",
                        "7-9%"
                )
        ));
        Mockito.when(orderRepository.findOrdersByUserIdAndTariffId(anyLong(), anyLong())).thenReturn(List.of(
                new Order(
                        1L,
                        UUID.randomUUID().toString(),
                        1L,
                        1L,
                        0.65,
                        Status.APPROVED,
                        Timestamp.valueOf(LocalDateTime.now()),
                        Timestamp.valueOf(LocalDateTime.now()),
                        0
                )
        ));
        Assertions.assertThrows(LoanAlreadyApprovedException.class, () -> orderService.createOrder(1L, 1L));
    }

    @Test
    void createOrderThrowsTryLaterException() {
        Mockito.when(tariffRepository.findTariffById(anyLong())).thenReturn(Optional.of(
                new Tariff(
                        1L,
                        "Потребительский",
                        "7-9%"
                )
        ));
        Mockito.when(orderRepository.findOrdersByUserIdAndTariffId(anyLong(), anyLong())).thenReturn(List.of(
                new Order(
                        1L,
                        UUID.randomUUID().toString(),
                        1L,
                        1L,
                        0.65,
                        Status.REFUSED,
                        Timestamp.valueOf(LocalDateTime.now().minusMinutes(2)),
                        Timestamp.valueOf(LocalDateTime.now().minusMinutes(1)),
                        0
                )
        ));
        Assertions.assertThrows(TryLaterException.class, () -> orderService.createOrder(1L, 1L));
    }

    @Test
    void createOrderCallsOrderRepo() {
        Mockito.when(tariffRepository.findTariffById(anyLong())).thenReturn(Optional.of(
                new Tariff(
                        1L,
                        "Потребительский",
                        "7-9%"
                )
        ));
        Mockito.when(orderRepository.findOrdersByUserIdAndTariffId(anyLong(), anyLong())).thenReturn(List.of(
                new Order(
                        1L,
                        UUID.randomUUID().toString(),
                        1L,
                        1L,
                        0.65,
                        Status.REFUSED,
                        Timestamp.valueOf(LocalDateTime.now().minusMinutes(5)),
                        Timestamp.valueOf(LocalDateTime.now().minusMinutes(4)),
                        0
                )
        ));
        orderService.createOrder(1L, 1L);
        Mockito.verify(orderRepository, Mockito.times(1)).save(any(Order.class));
    }

    @Test
    void getOrderStatusThrowsOrderNotFoundException() {
        Mockito.when(orderRepository.findOrderStatusByOrderId(anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.getOrderStatus(anyString()));
    }

    @Test
    void getOrderStatusReturnsStatus() {
        Mockito.when(orderRepository.findOrderStatusByOrderId(anyString())).thenReturn(Optional.of(Status.IN_PROGRESS));
        Assertions.assertEquals(Status.IN_PROGRESS, orderService.getOrderStatus(anyString()));
    }

    @Test
    void deleteOrderByUserIdAndOrderIdThrowsOrderNotFoundException() {
        Mockito.when(orderRepository.findOrderByUserIdAndOrderId(anyLong(), anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrderByUserIdAndOrderId(anyLong(), anyString()));
    }

    @Test
    void deleteOrderByUserIdAndOrderIdThrowsOrderImpossibleToDeleteException() {
        String fst = UUID.randomUUID().toString();
        String snd = UUID.randomUUID().toString();

        Mockito.when(orderRepository.findOrderByUserIdAndOrderId(1L, fst)).thenReturn(Optional.of(new Order(
                1L,
                fst,
                1L,
                1L,
                0.65,
                Status.REFUSED,
                Timestamp.valueOf(LocalDateTime.now().minusMinutes(5)),
                Timestamp.valueOf(LocalDateTime.now().minusMinutes(4)),
                0
        )));
        Mockito.when(orderRepository.findOrderByUserIdAndOrderId(2L, snd)).thenReturn(Optional.of(new Order(
                2L,
                snd,
                2L,
                1L,
                0.65,
                Status.APPROVED,
                Timestamp.valueOf(LocalDateTime.now().minusMinutes(2)),
                Timestamp.valueOf(LocalDateTime.now().minusMinutes(1)),
                0
        )));
        Assertions.assertAll(() -> {
            Assertions.assertThrows(OrderImpossibleToDeleteException.class, () -> orderService.deleteOrderByUserIdAndOrderId(1L, fst));
            Assertions.assertThrows(OrderImpossibleToDeleteException.class, () -> orderService.deleteOrderByUserIdAndOrderId(2L, snd));
        });
    }
}