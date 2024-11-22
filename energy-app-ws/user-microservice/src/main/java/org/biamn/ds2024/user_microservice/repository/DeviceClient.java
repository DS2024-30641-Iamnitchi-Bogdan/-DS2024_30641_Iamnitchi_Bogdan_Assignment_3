package org.biamn.ds2024.user_microservice.repository;

import jakarta.validation.Valid;
import org.biamn.ds2024.user_microservice.dto.user_device.DeviceUserRequestDTO;
import org.biamn.ds2024.user_microservice.dto.user_device.DeviceUserResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(
        name = "device-client",
        url = "http://${device.host}",
        path = "/api/v1/device/user",
        configuration = org.biamn.ds2024.user_microservice.config.FeignClientConfig.class
)
public interface DeviceClient {

    @PostMapping()
    ResponseEntity<DeviceUserResponseDTO> saveDeviceUser(
            @RequestBody @Valid DeviceUserRequestDTO deviceUserRequestDTO
    );

    @PutMapping("/{id}")
    ResponseEntity<DeviceUserResponseDTO> updateById(
            @PathVariable("id") UUID deviceUserId,
            @RequestBody DeviceUserRequestDTO deviceUserRequestDTO
    ); 

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteById(@PathVariable("id") UUID deviceUserId);
   
}
