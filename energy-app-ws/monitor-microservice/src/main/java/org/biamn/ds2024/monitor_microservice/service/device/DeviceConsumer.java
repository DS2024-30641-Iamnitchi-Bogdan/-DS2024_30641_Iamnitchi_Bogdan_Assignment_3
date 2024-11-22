package org.biamn.ds2024.monitor_microservice.service.device;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.biamn.ds2024.monitor_microservice.dto.device.DeviceRequestDTO;
import org.biamn.ds2024.monitor_microservice.dto.device.DeviceResponseDTO;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceConsumer {

    private final DeviceService deviceService;
    private final ObjectMapper objectMapper;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "", durable = "false", exclusive = "true", autoDelete = "true"),
            exchange = @Exchange(value = "${rabbitmq.exchange.device}", type = "${rabbitmq.exchange.type}"),
            key = "insert"
    ))
    public void consumeInsert(String message) {
        try {
            log.info("Received INSERT message: {}", message);

            DeviceRequestDTO deviceToBeAdded = objectMapper.readValue(message, DeviceRequestDTO.class);
            deviceService.save(deviceToBeAdded);

            log.info("Device inserted successfully: {}", deviceToBeAdded);
        } catch (Exception e) {
            log.error("Error processing INSERT message: {}", message, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "", durable = "false", exclusive = "true", autoDelete = "true"),
            exchange = @Exchange(value = "${rabbitmq.exchange.device}", type = "${rabbitmq.exchange.type}"),
            key = "update"
    ))
    public void consumeUpdate(String message) {
        try {
            log.info("Received UPDATE message: {}", message);

            DeviceRequestDTO deviceToUpdate = objectMapper.readValue(message, DeviceRequestDTO.class);

            DeviceResponseDTO device = deviceService.findById(deviceToUpdate.getId());
            if(device == null) {
                log.error("Device not found: {}", deviceToUpdate);
                return;
            }

            deviceService.updateById(device.getId(), deviceToUpdate);
            log.info("Device updated successfully: {}", deviceToUpdate);
        } catch (Exception e) {
            log.error("Error processing UPDATE message: {}", message, e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "", durable = "false", exclusive = "true", autoDelete = "true"),
            exchange = @Exchange(value = "${rabbitmq.exchange.device}", type = "${rabbitmq.exchange.type}"),
            key = "delete"
    ))
    public void consumeDelete(String message) {
        try {
            log.info("Received DELETE message: {}", message);

            UUID deviceId = objectMapper.readValue(message, UUID.class);
            deviceService.deleteById(deviceId);

            log.info("Device deleted successfully: {}", deviceId);
        } catch (Exception e) {
            log.error("Error processing DELETE message: {}", message, e);
        }
    }
}
