package com.example.creditservice.repository;

import com.example.creditservice.model.Tariff;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;

@Testcontainers
@SpringBootTest
class TariffRepositoryImplTest {
    @Container
    private static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.consumer.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.producer.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

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
    void createTariff() {
        Assertions.assertEquals(1, tariffRepository.createTariff(anyString(), anyString()));
    }
}