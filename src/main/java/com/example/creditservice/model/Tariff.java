package com.example.creditservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tariff {
    private Long id;
    private String type;
    private String interest_rate;
}
