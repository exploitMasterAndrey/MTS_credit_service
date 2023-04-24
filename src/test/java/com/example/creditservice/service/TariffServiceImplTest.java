package com.example.creditservice.service;

import com.example.creditservice.model.Tariff;
import com.example.creditservice.repository.TariffRepository;
import com.example.creditservice.service.impl.TariffServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class TariffServiceImplTest {
    @Mock
    private TariffRepository tariffRepository;
    private TariffService tariffService;

    @Test
    void getAllTariffs() {
        List<Tariff> tariffs = List.of(
                new Tariff(1L, "Потребительский", "7-9%"),
                new Tariff(2L, "Автокредит", "4-6%"),
                new Tariff(3L, "Ипотечный", "3-7%")
        );
        Mockito.when(tariffRepository.findAll()).thenReturn(tariffs);
        tariffService = new TariffServiceImpl(tariffRepository);
        Assertions.assertArrayEquals(tariffs.toArray(), tariffService.getAllTariffs().toArray(new Tariff[0]));
    }
}