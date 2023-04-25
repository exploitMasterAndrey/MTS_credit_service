package com.example.creditservice.service.impl;

import com.example.creditservice.controller.CreditController;
import com.example.creditservice.model.Tariff;
import com.example.creditservice.repository.TariffRepository;
import com.example.creditservice.service.TariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffServiceImpl implements TariffService {
    private final TariffRepository tariffRepository;

    @Override
    public List<Tariff> getAllTariffs() {
        return tariffRepository.findAll();
    }

    @Override
    public void createTariff(CreditController.CreateTariffRequest createTariffRequest) {
        tariffRepository.createTariff(createTariffRequest.type(), createTariffRequest.interest_rate());
    }
}
