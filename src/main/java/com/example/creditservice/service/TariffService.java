package com.example.creditservice.service;

import com.example.creditservice.controller.CreditController;
import com.example.creditservice.model.Tariff;

import java.util.List;

public interface TariffService {
    List<Tariff> getAllTariffs();

    void createTariff(CreditController.CreateTariffRequest createTariffRequest);
}
