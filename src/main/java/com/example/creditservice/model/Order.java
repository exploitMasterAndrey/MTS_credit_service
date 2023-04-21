package com.example.creditservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Order {
    private Long id;
    private String orderId;
    private Long userId;
    private Long tariffId;
    private Double creditRating;
    private Status status;
    private Timestamp timeInsert;
    private Timestamp timeUpdate;

}
