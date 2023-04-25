package com.example.creditservice.controller;

import com.example.creditservice.config.auth.JWTAuthFilter;
import com.example.creditservice.controller.exceptionHandler.ExceptionHandler;
import com.example.creditservice.controller.impl.CreditControllerImpl;
import com.example.creditservice.exception.*;
import com.example.creditservice.model.Status;
import com.example.creditservice.model.Tariff;
import com.example.creditservice.service.impl.OrderServiceImpl;
import com.example.creditservice.service.impl.TariffServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreditControllerImpl.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class CreditControllerImplTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TariffServiceImpl tariffService;
    @MockBean
    private OrderServiceImpl orderService;
    @MockBean
    private JWTAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long tariffId = 1L, userId = 1L;
    private static String orderId = UUID.randomUUID().toString();

    @Test
    @SneakyThrows
    void getTariffs() {
        List<Tariff> tariffs = List.of(
                new Tariff(1L, "Потребительский", "7-9%"),
                new Tariff(2L, "Автокредит", "4-6%"),
                new Tariff(3L, "Ипотечный", "3-7%")
        );
        Mockito.when(tariffService.getAllTariffs()).thenReturn(tariffs);
        this.mockMvc
                .perform(get("/loan-service/getTariffs"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new CreditControllerImpl.TariffResponse(tariffs))));
    }

    @Test
    @SneakyThrows
    void createOrderReturnsOrderId() {
        Mockito.when(orderService.createOrder(tariffId, userId)).thenReturn(orderId);

        Map<String, Long> properties = new HashMap<>();
        properties.put("tariffId", tariffId);
        properties.put("userId", userId);
        String requestJson = objectMapper.writeValueAsString(properties);

        this.mockMvc
                .perform(post("/loan-service/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new CreditControllerImpl.CreateOrderResponse(orderId))));
    }

    @Test
    @SneakyThrows
    void createOrderReturnsTariffNotFoundError() {
        Mockito.when(orderService.createOrder(tariffId, userId)).thenThrow(new TariffNotFoundException("Тариф не найден"));

        Map<String, Long> properties = new HashMap<>();
        properties.put("tariffId", tariffId);
        properties.put("userId", userId);
        String requestJson = objectMapper.writeValueAsString(properties);

        this.mockMvc
                .perform(post("/loan-service/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(new ExceptionHandler.ExceptionResponse("TARIFF_NOT_FOUND", "Тариф не найден"))));
    }

    @Test
    @SneakyThrows
    void createOrderReturnsLoanConsiderationError() {
        Mockito.when(orderService.createOrder(tariffId, userId)).thenThrow(new LoanConsiderationException("Заявка в процессе обработки"));

        Map<String, Long> properties = new HashMap<>();
        properties.put("tariffId", tariffId);
        properties.put("userId", userId);
        String requestJson = objectMapper.writeValueAsString(properties);

        this.mockMvc
                .perform(post("/loan-service/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(new ExceptionHandler.ExceptionResponse("LOAN_CONSIDERATION", "Заявка в процессе обработки"))));
    }

    @Test
    @SneakyThrows
    void createOrderReturnsLoanAlreadyApprovedError() {
        Mockito.when(orderService.createOrder(anyLong(), anyLong())).thenThrow(new LoanAlreadyApprovedException("Заявка уже была одобрена"));

        Map<String, Long> properties = new HashMap<>();
        properties.put("tariffId", tariffId);
        properties.put("userId", userId);
        String requestJson = objectMapper.writeValueAsString(properties);

        this.mockMvc
                .perform(post("/loan-service/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(new ExceptionHandler.ExceptionResponse("LOAN_ALREADY_APPROVED", "Заявка уже была одобрена"))));
    }

    @Test
    @SneakyThrows
    void createOrderReturnsTryLaterError() {
        Mockito.when(orderService.createOrder(anyLong(), anyLong())).thenThrow(new TryLaterException("Попробуйте позже"));

        Map<String, Long> properties = new HashMap<>();
        properties.put("tariffId", tariffId);
        properties.put("userId", userId);
        String requestJson = objectMapper.writeValueAsString(properties);

        this.mockMvc
                .perform(post("/loan-service/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(new ExceptionHandler.ExceptionResponse("TRY_LATER", "Попробуйте позже"))));
    }

    @Test
    @SneakyThrows
    void getOrderStatusReturnsStatus() {
        Mockito.when(orderService.getOrderStatus(orderId)).thenReturn(Status.APPROVED);
        this.mockMvc
                .perform(get("/loan-service/getStatusOrder")
                        .param("orderId", orderId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new CreditControllerImpl.GetOrderStatusResponse(Status.APPROVED))));
    }

    @Test
    @SneakyThrows
    void getOrderStatusReturnsOrderNotFoundError() {
        Mockito.when(orderService.getOrderStatus(orderId)).thenThrow(new OrderNotFoundException("Заявка не найдена"));
        this.mockMvc
                .perform(get("/loan-service/getStatusOrder")
                        .param("orderId", orderId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(new ExceptionHandler.ExceptionResponse("ORDER_NOT_FOUND", "Заявка не найдена"))));
    }

    @Test
    @SneakyThrows
    void deleteOrderReturnsOkStatus() {
        Map<String, String> properties = new HashMap<>();
        properties.put("orderId", orderId);
        properties.put("userId", String.valueOf(userId));
        String requestJson = objectMapper.writeValueAsString(properties);

        this.mockMvc
                .perform(delete("/loan-service/deleteOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void deleteOrderReturnsOrderNotFoundError() {
        Mockito.doThrow(new OrderNotFoundException("Заявка не найдена")).when(orderService).deleteOrderByUserIdAndOrderId(userId, orderId);

        Map<String, String> properties = new HashMap<>();
        properties.put("orderId", orderId);
        properties.put("userId", String.valueOf(userId));
        String requestJson = objectMapper.writeValueAsString(properties);

        this.mockMvc
                .perform(delete("/loan-service/deleteOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(new ExceptionHandler.ExceptionResponse("ORDER_NOT_FOUND", "Заявка не найдена"))));
    }

    @Test
    @SneakyThrows
    void deleteOrderReturnsOrderImpossibleToDeleteError() {
        Mockito.doThrow(new OrderImpossibleToDeleteException("Невозможно удалить заявку")).when(orderService).deleteOrderByUserIdAndOrderId(userId, orderId);

        Map<String, String> properties = new HashMap<>();
        properties.put("orderId", orderId);
        properties.put("userId", String.valueOf(userId));
        String requestJson = objectMapper.writeValueAsString(properties);

        this.mockMvc
                .perform(delete("/loan-service/deleteOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(new ExceptionHandler.ExceptionResponse("ORDER_IMPOSSIBLE_TO_DELETE", "Невозможно удалить заявку"))));
    }
}