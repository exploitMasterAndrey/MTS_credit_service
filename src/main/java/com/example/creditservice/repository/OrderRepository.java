package com.example.creditservice.repository;

import com.example.creditservice.model.Order;
import com.example.creditservice.model.Status;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    RowMapper<Order> order = (rs, rowNum) -> new Order(
            rs.getLong(1),
            rs.getString(2),
            rs.getLong(3),
            rs.getLong(4),
            rs.getDouble(5),
            Status.valueOf(rs.getString(6)),
            rs.getTimestamp(7),
            rs.getTimestamp(8)
    );
    RowMapper<Status> status = (rs, rowNum) -> Status.valueOf(rs.getString(1));

    int save(Order order);

    List<Order> findOrdersByUserIdAndTariffId(Long userId, Long tariffId);

    Optional<Status> findOrderStatusByOrderId(String orderId);

    int updateStatusWhereStatusInProgress();

    Optional<Order> findOrderByUserIdAndOrderId(Long userId, String orderId);

    int deleteOrderByOrderId(String orderId);
}
