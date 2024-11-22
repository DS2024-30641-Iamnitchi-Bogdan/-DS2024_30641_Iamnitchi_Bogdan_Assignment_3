package org.biamn.ds2024.device_microservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.biamn.ds2024.device_microservice.config.RabbitMQConfig;
import org.biamn.ds2024.device_microservice.dto.device.DeviceRequestDTO;
import org.biamn.ds2024.device_microservice.dto.device.DeviceResponseDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.device}")
    private String exchangeName;

    public void produceMessage(DeviceResponseDTO deviceResponseDTO, String routingKey) {
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.convertAndSend(exchangeName, routingKey, deviceResponseDTO);
        System.out.println("Message sent to exchange: " + exchangeName);
    }

    public void produceMessage(String message, String routingKey) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
        System.out.println("Message sent to exchange: " + exchangeName);
    }

}
