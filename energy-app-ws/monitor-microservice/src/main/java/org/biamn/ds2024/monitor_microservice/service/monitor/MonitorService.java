package org.biamn.ds2024.monitor_microservice.service.monitor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.biamn.ds2024.monitor_microservice.dto.graph.GraphResponseDTO;
import org.biamn.ds2024.monitor_microservice.dto.monitor.MonitorRequestDTO;
import org.biamn.ds2024.monitor_microservice.dto.monitor.MonitorResponseDTO;
import org.biamn.ds2024.monitor_microservice.exceptions.exception.model.ExceptionCode;
import org.biamn.ds2024.monitor_microservice.mapper.MonitorMapper;
import org.biamn.ds2024.monitor_microservice.model.DeviceEntity;
import org.biamn.ds2024.monitor_microservice.model.MonitorEntity;
import org.biamn.ds2024.monitor_microservice.repository.DeviceRepository;
import org.biamn.ds2024.monitor_microservice.repository.MonitorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.biamn.ds2024.monitor_microservice.exceptions.exception.model.ResourceNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorService {
    private final DeviceRepository deviceRepository;
    private final MonitorRepository monitorRepository;
    private final MonitorMapper monitorMapper;

    @Value("${spring.application.name:BACKEND}")
    private String applicationName;

    public List<MonitorResponseDTO> findAll() {
        log.info("Getting all devices for {}", applicationName);
        List<MonitorEntity> monitorEntityList = monitorRepository.findAll();
        return monitorMapper.monitorEntityListToMonitorResponseDTOList(monitorEntityList);
    }

    public MonitorResponseDTO save(MonitorRequestDTO monitorRequestDTO) {
        DeviceEntity device =  deviceRepository.findById(monitorRequestDTO.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR001_DEVICE_NOT_FOUND.getMessage(),
                        monitorRequestDTO.getDeviceId()
                )));
        MonitorEntity monitorToBeAdded = monitorMapper.monitorRequestDTOToMonitorEntity(monitorRequestDTO, device);
        monitorRepository.save(monitorToBeAdded);
        return monitorMapper.monitorEntityToMonitorResponseDTO(monitorToBeAdded);
    }

    public MonitorResponseDTO findByMonitorId(UUID monitorId) {
        return monitorRepository.findById(monitorId)
                .map(monitorMapper::monitorEntityToMonitorResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR001_DEVICE_NOT_FOUND.getMessage(),
                        monitorId
                )));
    }

    public List<MonitorResponseDTO> findByUserId(UUID userId) {
        return monitorRepository.findMonitorEntitiesByDeviceId(userId).stream()
                .map(monitorMapper::monitorEntityToMonitorResponseDTO)
                .collect(Collectors.toList());
    }

    public GraphResponseDTO findGraphDataForDevice(UUID deviceId, LocalDate dateTime) {
        LocalDateTime startOfDay = dateTime.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // Fetch all monitor data for the device within the day's time range
        List<MonitorEntity> monitorEntities = monitorRepository.findAllByDay(deviceId, startOfDay, endOfDay);

        // Define the DateTimeFormatter for hours in "HH:mm" format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        // Map the hours to the "HH:mm" format
        String[] hours = monitorEntities.stream()
                .map(entity -> entity.getTimestamp().format(formatter))  // Format timestamp to "HH:mm"
                .toArray(String[]::new);

        // Map the average consumption values
        Double[] values = monitorEntities.stream()
                .map(MonitorEntity::getAvg_consumption)
                .toArray(Double[]::new);

        // Return the response DTO with the formatted hours and values
        return GraphResponseDTO.builder()
                .hours(hours)
                .values(values)
                .build();
    }

    public MonitorResponseDTO updateById(UUID deviceId, MonitorRequestDTO monitorRequestDTO){
        MonitorEntity monitorEntity = monitorRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR001_DEVICE_NOT_FOUND.getMessage(),
                        deviceId
                )));

        monitorEntity.setTimestamp(monitorRequestDTO.getTimestamp());
        monitorEntity.setAvg_consumption(monitorRequestDTO.getAvg_consumption());
        monitorEntity.setMax_consumption(monitorRequestDTO.getMax_consumption());
        monitorEntity.setDevice(deviceRepository.findById(monitorRequestDTO.getDeviceId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR001_DEVICE_NOT_FOUND.getMessage(),
                        monitorRequestDTO.getDeviceId()
                ))));

        MonitorEntity updatedMonitorEntity = monitorRepository.save(monitorEntity);
        return monitorMapper.monitorEntityToMonitorResponseDTO(updatedMonitorEntity);
    }

    public void deleteById(UUID deviceId){
        if (!monitorRepository.existsById(deviceId)) {
            throw new ResourceNotFoundException(String.format(
                    ExceptionCode.ERR001_DEVICE_NOT_FOUND.getMessage(), deviceId));
        }
        monitorRepository.deleteById(deviceId);
    }

}