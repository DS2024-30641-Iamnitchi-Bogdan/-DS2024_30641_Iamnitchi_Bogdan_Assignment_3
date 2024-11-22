package org.biamn.ds2024.monitor_microservice.service.monitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.biamn.ds2024.monitor_microservice.dto.measurement.MeasurementDTO;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeasurementConsumer {

    private final MeasurementService measurementService;
    private final ObjectMapper objectMapper;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "", durable = "false", exclusive = "true", autoDelete = "true"),
            exchange = @Exchange(value = "${rabbitmq.exchange.sensor}", type = "${rabbitmq.exchange.type}")
    ))
    public void consumeInsert(String message) {
        try {
            log.info("Received message: {}", message);

            MeasurementDTO measurement = objectMapper.readValue(message, MeasurementDTO.class);
            measurementService.processMeasurement(measurement);

            log.info("Measurement processed successfully: {}", measurement);
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
        }
    }
}
