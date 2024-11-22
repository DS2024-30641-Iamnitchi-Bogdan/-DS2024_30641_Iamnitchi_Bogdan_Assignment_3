package org.biamn.ds2024.device_microservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.biamn.ds2024.device_microservice.dto.CollectionResponseDTO;
import org.biamn.ds2024.device_microservice.dto.device.DeviceRequestDTO;
import org.biamn.ds2024.device_microservice.dto.device.DeviceResponseDTO;
import org.biamn.ds2024.device_microservice.dto.PageRequestDTO;
import org.biamn.ds2024.device_microservice.service.DeviceProducer;
import org.biamn.ds2024.device_microservice.service.DeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    private final DeviceProducer deviceProducer;

    @GetMapping("/publish/{routingKey}")
    public ResponseEntity<String> publishMessage(@PathVariable("routingKey") String routingKey, @RequestBody DeviceResponseDTO deviceRequestDTO) {
        deviceProducer.produceMessage(deviceRequestDTO, routingKey);
        return new ResponseEntity<>("Message published", HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<DeviceResponseDTO>> findAll() {
        return new ResponseEntity<>(
                deviceService.findAll(),
                HttpStatus.OK
        );
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<DeviceResponseDTO> findByDeviceId(@PathVariable("deviceId") UUID deviceId) {
        return new ResponseEntity<>(
                deviceService.findByDeviceId(deviceId),
                HttpStatus.OK
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DeviceResponseDTO>> findByUserId(@PathVariable("userId") UUID userId) {
        return new ResponseEntity<>(
                deviceService.findByUserId(userId),
                HttpStatus.OK
        );
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<DeviceResponseDTO> findByLocation(
            @PathVariable("location") String deviceLocation
    ) {
        return new ResponseEntity<>(
                deviceService.findByLocation(deviceLocation),
                HttpStatus.OK
        );
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<DeviceResponseDTO> findByName(
            @PathVariable("name") String deviceName
    ) {
        return new ResponseEntity<>(
                deviceService.findByName(deviceName),
                HttpStatus.OK
        );
    }

    @PostMapping()
    public ResponseEntity<DeviceResponseDTO> saveDevice(
            @RequestBody @Valid DeviceRequestDTO deviceRequestDTO
    )
    {
        System.out.println("deviceRequestDTO: " + deviceRequestDTO.getUser_id());
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

    @GetMapping("/paged")
    public ResponseEntity<CollectionResponseDTO<DeviceResponseDTO>> findAllPaged(
            @Valid PageRequestDTO page
    ) {
        return new ResponseEntity<>(
                deviceService.findAllPaged(page),
                HttpStatus.OK
        );
    }

}
