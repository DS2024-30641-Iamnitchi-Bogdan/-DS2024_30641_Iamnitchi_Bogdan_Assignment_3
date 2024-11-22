package org.biamn.ds2024.monitor_microservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.biamn.ds2024.monitor_microservice.dto.graph.GraphResponseDTO;
import org.biamn.ds2024.monitor_microservice.dto.monitor.MonitorRequestDTO;
import org.biamn.ds2024.monitor_microservice.dto.monitor.MonitorResponseDTO;
import org.biamn.ds2024.monitor_microservice.service.monitor.MonitorService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/v1/monitor")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class MonitorController {

    private final MonitorService monitorService;

    @GetMapping("")
    public ResponseEntity<List<MonitorResponseDTO>> findAll() {
        log.info("Fetching all monitors");
        List<MonitorResponseDTO> monitors = monitorService.findAll();
        return new ResponseEntity<>(monitors, HttpStatus.OK);
    }

    @GetMapping("/{monitorId}")
    public ResponseEntity<MonitorResponseDTO> findByMonitorId(@PathVariable("monitorId") UUID monitorId) {
        log.info("Fetching monitor with ID: {}", monitorId);
        MonitorResponseDTO monitorResponse = monitorService.findByMonitorId(monitorId);
        return new ResponseEntity<>(monitorResponse, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MonitorResponseDTO>> findByUserId(@PathVariable("userId") UUID userId) {
        log.info("Fetching monitors for user with ID: {}", userId);
        List<MonitorResponseDTO> monitors = monitorService.findByUserId(userId);
        return new ResponseEntity<>(monitors, HttpStatus.OK);
    }

    @GetMapping("/graph")
    public ResponseEntity<GraphResponseDTO> findGraphDataForDevice(
            @RequestParam("deviceId") UUID deviceId,
            @RequestParam("date") LocalDate date) {
        log.info("Fetching monitors for timestamp: {}", date);
        GraphResponseDTO allByDay = monitorService.findGraphDataForDevice(deviceId, date);
        return new ResponseEntity<>(allByDay, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<MonitorResponseDTO> saveMonitor(
            @RequestBody @Valid MonitorRequestDTO monitorRequestDTO
    ) {
        log.info("Saving new monitor with user ID: {}", monitorRequestDTO.getDeviceId());
        MonitorResponseDTO savedMonitor = monitorService.save(monitorRequestDTO);
        return new ResponseEntity<>(savedMonitor, HttpStatus.CREATED);
    }

    @PutMapping("/{monitorId}")
    public ResponseEntity<MonitorResponseDTO> updateById(
            @PathVariable("monitorId") UUID monitorId,
            @RequestBody @Valid MonitorRequestDTO monitorRequestDTO
    ) {
        log.info("Updating monitor with ID: {}", monitorId);
        MonitorResponseDTO updatedMonitor = monitorService.updateById(monitorId, monitorRequestDTO);
        return new ResponseEntity<>(updatedMonitor, HttpStatus.OK);
    }

    @DeleteMapping("/{monitorId}")
    public ResponseEntity<Void> deleteById(@PathVariable("monitorId") UUID monitorId) {
        log.info("Deleting monitor with ID: {}", monitorId);
        monitorService.deleteById(monitorId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}