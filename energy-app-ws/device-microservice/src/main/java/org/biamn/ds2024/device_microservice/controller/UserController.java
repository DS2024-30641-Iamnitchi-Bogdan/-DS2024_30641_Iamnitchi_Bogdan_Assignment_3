package org.biamn.ds2024.device_microservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.biamn.ds2024.device_microservice.dto.user_device.UserRequestDTO;
import org.biamn.ds2024.device_microservice.dto.user_device.UserResponseDTO;
import org.biamn.ds2024.device_microservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/device/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        return new ResponseEntity<>(
                userService.findAll(),
                HttpStatus.OK
        );
    }

    @PostMapping()
    public ResponseEntity<UserResponseDTO> saveDeviceUser(
            @RequestBody @Valid UserRequestDTO userRequestDTO
    ) {
        return new ResponseEntity<>(
                userService.save(userRequestDTO),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateById(
            @PathVariable("id") UUID deviceUserId,
            @RequestBody UserRequestDTO userRequestDTO
    ) {
        return new ResponseEntity<>(
                userService.updateById(deviceUserId, userRequestDTO),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID deviceUserId)
    {
        userService.deleteById(deviceUserId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
