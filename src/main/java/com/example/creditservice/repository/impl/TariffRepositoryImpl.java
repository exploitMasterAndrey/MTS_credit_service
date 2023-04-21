package com.example.creditservice.repository.impl;

import com.example.creditservice.model.Tariff;
import com.example.creditservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TariffRepositoryImpl implements TariffRepository {
    private final JdbcTemplate jdbcTemplate;
    private static String GET_ALL = "SELECT * FROM tariff";
    private static String GET_TARIFF_BY_ID = "SELECT * from tariff WHERE id = ?";

    @Override
    public List<Tariff> findAll() {
        return jdbcTemplate.query(
                GET_ALL,
                TariffRepository.tariff
        );
    }

    @Override
    public Optional<Tariff> findTariffById(Long id) {
        return jdbcTemplate.query(con -> {
                    PreparedStatement ps = con.prepareStatement(GET_TARIFF_BY_ID);
                    ps.setLong(1, id);
                    return ps;
                },
                TariffRepository.tariff
        ).stream().findFirst();
    }
}
