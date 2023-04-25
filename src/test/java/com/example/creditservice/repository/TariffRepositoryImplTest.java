package com.example.creditservice.repository;

import com.example.creditservice.model.Tariff;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;

@Testcontainers
@SpringBootTest
class TariffRepositoryImplTest {
    @Autowired
    private TariffRepository tariffRepository;

    @Test
    void findAll() {
        List<Tariff> expected = List.of(new Tariff(1L, "Потребилельский", "7-9%"),
                new Tariff(2L, "Автокредит", "4-6%"),
                new Tariff(3L, "Ипотечный", "3-7%"));
        Assertions.assertArrayEquals(expected.toArray(), tariffRepository.findAll().toArray());
    }

    @Test
    void findTariffById() {
        Tariff expected = new Tariff(1L, "Потребилельский", "7-9%");
        Assertions.assertAll(() -> {
            Assertions.assertEquals(expected, tariffRepository.findTariffById(1L).get());
            Assertions.assertTrue(tariffRepository.findTariffById(10L).isEmpty());
        });
    }

    @Test
    void createTariff(){
        Assertions.assertEquals(1, tariffRepository.createTariff(anyString(), anyString()));
    }
}