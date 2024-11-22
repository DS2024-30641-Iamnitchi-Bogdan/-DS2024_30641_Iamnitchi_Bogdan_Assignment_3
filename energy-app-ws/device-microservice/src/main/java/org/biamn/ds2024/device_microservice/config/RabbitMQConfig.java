package org.biamn.ds2024.device_microservice.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.device}")
    private String exchangeName;

    @Bean
    public TopicExchange fanoutExchange() {
        return new TopicExchange(exchangeName);
    }
}
