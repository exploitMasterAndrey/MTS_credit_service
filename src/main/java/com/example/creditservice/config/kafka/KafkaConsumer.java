package com.example.creditservice.config.kafka;

import com.example.creditservice.model.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    @KafkaListener(
            topics = "statisticsTopic",
            groupId = "group1"
    )
    public void getMessage(Order message){
        System.out.println("Received:" + message.toString());
    }
}
