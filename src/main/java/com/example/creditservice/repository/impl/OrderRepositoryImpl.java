package com.example.creditservice.repository.impl;

import com.example.creditservice.model.Order;
import com.example.creditservice.model.Status;
import com.example.creditservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_QUERY = "INSERT INTO loan_order(order_id, user_id, tariff_id, credit_rating, status, time_insert, time_update) VALUES" +
            "(?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_ORDERS_BY_USER_ID = "SELECT * FROM loan_order WHERE user_id = ? AND tariff_id = ?";
    private static final String FIND_ORDER_STATUS_BY_ORDER_ID = "SELECT status FROM loan_order WHERE order_id LIKE ?";
    private static final String UPDATE_STATUS_WHERE_STATUS_IN_PROGRESS = "UPDATE loan_order SET " +
            "status = CASE WHEN random() < 0.5 THEN 'APPROVED' ELSE 'REFUSED' END, " +
            "time_update = now() " +
            "WHERE status LIKE 'IN_PROGRESS'";
    private static final String FIND_ORDER_BY_USER_ID_AND_ORDER_ID = "SELECT * FROM loan_order WHERE user_id = ? AND order_id LIKE ?";
    private static final String DELETE_ORDER_BY_ORDER_ID = "DELETE FROM loan_order WHERE order_id LIKE ?";

    @Override
    public int save(Order order) {
        return jdbcTemplate.update(INSERT_QUERY, order.getOrderId(), order.getUserId(), order.getTariffId(), order.getCreditRating(), order.getStatus().name(), order.getTimeInsert(), order.getTimeUpdate());
    }

    @Override
    public List<Order> findOrdersByUserIdAndTariffId(Long userId, Long tariffId) {
        return jdbcTemplate.query(FIND_ORDERS_BY_USER_ID, OrderRepository.order, userId, tariffId);
    }

    @Override
    public Optional<Status> findOrderStatusByOrderId(String orderId) {
        return jdbcTemplate.query(FIND_ORDER_STATUS_BY_ORDER_ID, OrderRepository.status, orderId).stream().findFirst();
    }

    @Override
    public int updateStatusWhereStatusInProgress() {
        return jdbcTemplate.update(UPDATE_STATUS_WHERE_STATUS_IN_PROGRESS);
    }

    @Override
    public Optional<Order> findOrderByUserIdAndOrderId(Long userId, String orderId) {
        return jdbcTemplate.query(FIND_ORDER_BY_USER_ID_AND_ORDER_ID, OrderRepository.order, userId, orderId).stream().findFirst();
    }

    @Override
    public int deleteOrderByOrderId(String orderId) {
        return jdbcTemplate.update(DELETE_ORDER_BY_ORDER_ID, orderId);
    }
}
