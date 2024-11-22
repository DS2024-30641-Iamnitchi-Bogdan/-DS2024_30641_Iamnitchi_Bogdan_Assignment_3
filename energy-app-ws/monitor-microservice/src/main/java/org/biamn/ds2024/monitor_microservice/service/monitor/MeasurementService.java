package org.biamn.ds2024.monitor_microservice.service.monitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.biamn.ds2024.monitor_microservice.dto.measurement.MeasurementDTO;
import org.biamn.ds2024.monitor_microservice.websockets.NotificationWebSocketHandler;
import org.biamn.ds2024.monitor_microservice.model.DeviceEntity;
import org.biamn.ds2024.monitor_microservice.model.MonitorEntity;
import org.biamn.ds2024.monitor_microservice.repository.DeviceRepository;
import org.biamn.ds2024.monitor_microservice.repository.MonitorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeasurementService {

    private final NotificationWebSocketHandler notificationWebSocketHandler;

    private final MonitorRepository monitorRepository;
    private final DeviceRepository deviceRepository;

    private final Map<UUID, List<MeasurementDTO>> measurementBuffer = new ConcurrentHashMap<>();
    private final Map<UUID, MeasurementDTO> lastMeasurement = new ConcurrentHashMap<>();

    public void processMeasurement(MeasurementDTO measurementDTO) {
        UUID deviceId = measurementDTO.getDeviceId();

        MeasurementDTO previousMeasurement = lastMeasurement.put(deviceId, measurementDTO);
        if (previousMeasurement == null) {
            return;
        }

        double consumedValue = measurementDTO.getValue() - previousMeasurement.getValue();
        if (consumedValue < 0) {
            log.warn("Invalid consumed value (negative): {}", consumedValue);
            return;
        }

        MeasurementDTO consumptionMeasurement = MeasurementDTO.builder()
                .timestamp(measurementDTO.getTimestamp())
                .deviceId(measurementDTO.getDeviceId())
                .value(consumedValue)
                .build();

        measurementBuffer.computeIfAbsent(deviceId, key -> new CopyOnWriteArrayList<>()).add(consumptionMeasurement);
        checkAndProcessBuffer(deviceId);
    }

    private void checkAndProcessBuffer(UUID deviceId) {
        List<MeasurementDTO> measurements = measurementBuffer.get(deviceId);
        if (measurements == null || measurements.isEmpty()) {
            return;
        }

//        LocalDateTime windowStart = measurements.get(0).getTimestamp().toLocalDateTime().withMinute(0).withSecond(0).withNano(0);
        LocalDateTime windowStart = measurements.get(0).getTimestamp().toLocalDateTime().withMinute(0).withSecond(0).withNano(0);
        LocalDateTime windowEnd = windowStart.plusHours(1);

        LocalDateTime latestTimestamp = measurements.get(measurements.size() - 1).getTimestamp().toLocalDateTime();
        if (!latestTimestamp.isAfter(windowEnd)) {
            return;
        }

        List<MeasurementDTO> windowMeasurements = measurements.stream()
                .filter(m -> {
                    LocalDateTime ts = m.getTimestamp().toLocalDateTime();
                    return ts.isEqual(windowStart) || (ts.isAfter(windowStart) && ts.isBefore(windowEnd));
                })
                .toList();

        measurements.removeAll(windowMeasurements);
        if (windowMeasurements.isEmpty()) {
            return;
        }

        DeviceEntity device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found for ID: " + deviceId));

        double avgConsumption = windowMeasurements.stream().mapToDouble(MeasurementDTO::getValue).average().orElse(0.0);
        double maxConsumption = windowMeasurements.stream().mapToDouble(MeasurementDTO::getValue).max().orElse(0.0);

        if (maxConsumption > device.getMax_consumption()) {
            log.warn("Max consumption {} exceeded the device's max consumption {} for device ID: {}",
                    maxConsumption, device.getMax_consumption(), device.getId());
            String notificationMessage = String.format("Warning: Device %s from %s register an max consumption %.2f this exceeded the device's max consumption %.2f",
                    device.getName(), device.getLocation(), maxConsumption, device.getMax_consumption());
            notificationWebSocketHandler.sendNotification(device.getUser_id(), notificationMessage);
        }

        MonitorEntity monitorEntity = MonitorEntity.builder()
                .timestamp(windowStart)
                .avg_consumption(avgConsumption)
                .max_consumption(maxConsumption)
                .device(device)
                .build();

        monitorRepository.save(monitorEntity);
        log.info("Monitor data saved: {}", monitorEntity);
    }
}
