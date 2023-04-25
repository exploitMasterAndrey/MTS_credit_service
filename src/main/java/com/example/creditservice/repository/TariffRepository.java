package com.example.creditservice.repository;

import com.example.creditservice.model.Tariff;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

public interface TariffRepository {
    RowMapper<Tariff> tariff = (rs, rowNum) -> new Tariff(
            rs.getLong(1),
            rs.getString(2),
            rs.getString(3)
    );

    List<Tariff> findAll();

    Optional<Tariff> findTariffById(Long id);

    int createTariff(String type, String interest_rate);
}
