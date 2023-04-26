package com.example.creditservice.repository.impl;

import com.example.creditservice.model.Order;
import com.example.creditservice.model.Status;
import com.example.creditservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_QUERY = "INSERT INTO loan_order(order_id, user_id, tariff_id, credit_rating, status, time_insert, time_update, sent_to_stream) VALUES" +
            "(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_ORDERS_BY_USER_ID = "SELECT * FROM loan_order WHERE user_id = ? AND tariff_id = ?";
    private static final String FIND_ORDER_STATUS_BY_ORDER_ID = "SELECT status FROM loan_order WHERE order_id LIKE ?";
    private static final String UPDATE_STATUS_WHERE_STATUS_IN_PROGRESS = "UPDATE loan_order SET " +
            "status = CASE WHEN random() < 0.5 THEN 'APPROVED' ELSE 'REFUSED' END, " +
            "time_update = now() " +
            "WHERE status LIKE 'IN_PROGRESS'";
    private static final String FIND_ORDERS_BY_STATUS = "SELECT * FROM loan_order WHERE status LIKE ?";
    private static final String UPDATE_STATUS_BY_ORDER_ID = "UPDATE loan_order SET status = ?, time_update = ? WHERE order_id LIKE ?";
    private static final String FIND_ORDER_BY_USER_ID_AND_ORDER_ID = "SELECT * FROM loan_order WHERE user_id = ? AND order_id LIKE ?";
    private static final String DELETE_ORDER_BY_ORDER_ID = "DELETE FROM loan_order WHERE order_id LIKE ?";
    private static final String FIND_ORDERS_TO_SEND = "SELECT * FROM loan_order WHERE status IN ('APPROVED', 'REFUSED') AND sent_to_stream = 0";
    private static final String UPDATE_ORDER_SENDING = "UPDATE loan_order SET sent_to_stream = 1 WHERE id = ?";

    @Override
    public int save(Order order) {
        return jdbcTemplate.update(INSERT_QUERY, order.getOrderId(), order.getUserId(), order.getTariffId(), order.getCreditRating(), order.getStatus().name(), order.getTimeInsert(), order.getTimeUpdate(), order.getSentToStream());
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
    public List<Order> findOrdersWhereStatus(Status status){
        return jdbcTemplate.query(FIND_ORDERS_BY_STATUS, OrderRepository.order, status.name());
    }

    @Override
    public List<Order> findOrdersToSend() {
        return jdbcTemplate.query(FIND_ORDERS_TO_SEND, OrderRepository.order);
    }

    @Override
    public int[] updateOrderStatus(List<Order> orders){
         return jdbcTemplate.batchUpdate(
                UPDATE_STATUS_BY_ORDER_ID,
                new BatchPreparedStatementSetter() {
                    public Status setStatus(){
                        return Math.random() < 0.5 ? Status.APPROVED : Status.REFUSED;
                    }
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, setStatus().name());
                        ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                        ps.setString(3, orders.get(i).getOrderId());
                    }
                    @Override
                    public int getBatchSize() {
                        return orders.size();
                    }
                }
        );
    }

    @Override
    public Optional<Order> findOrderByUserIdAndOrderId(Long userId, String orderId) {
        return jdbcTemplate.query(FIND_ORDER_BY_USER_ID_AND_ORDER_ID, OrderRepository.order, userId, orderId).stream().findFirst();
    }

    @Override
    public int deleteOrderByOrderId(String orderId) {
        return jdbcTemplate.update(DELETE_ORDER_BY_ORDER_ID, orderId);
    }

    @Override
    public int updateOrderSending(Long id) {
        return jdbcTemplate.update(UPDATE_ORDER_SENDING, id);
    }
}
