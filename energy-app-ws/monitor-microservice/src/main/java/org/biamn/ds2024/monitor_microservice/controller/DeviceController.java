package org.biamn.ds2024.monitor_microservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.biamn.ds2024.monitor_microservice.dto.device.DeviceRequestDTO;
import org.biamn.ds2024.monitor_microservice.dto.device.DeviceResponseDTO;
import org.biamn.ds2024.monitor_microservice.service.device.DeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/monitor/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping("")
    public ResponseEntity<List<DeviceResponseDTO>> findAll() {
        return new ResponseEntity<>(
                deviceService.findAll(),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponseDTO> findById(@PathVariable("id") UUID deviceId) {
        return new ResponseEntity<>(
                deviceService.findById(deviceId),
                HttpStatus.OK
        );
    }

    @PostMapping()
    public ResponseEntity<DeviceResponseDTO> saveDevice(
            @RequestBody @Valid DeviceRequestDTO deviceRequestDTO
    ) {
        return new ResponseEntity<>(
                deviceService.save(deviceRequestDTO),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponseDTO> updateById(
            @PathVariable("id") UUID deviceId,
            @RequestBody DeviceRequestDTO deviceRequestDTO
    ) {
        return new ResponseEntity<>(
                deviceService.updateById(deviceId, deviceRequestDTO),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID deviceId)
    {
        deviceService.deleteById(deviceId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
