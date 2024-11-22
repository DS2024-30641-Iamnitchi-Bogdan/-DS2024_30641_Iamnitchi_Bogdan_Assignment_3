package org.biamn.ds2024.user_microservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.biamn.ds2024.user_microservice.dto.CollectionResponseDTO;
import org.biamn.ds2024.user_microservice.dto.PageRequestDTO;
import org.biamn.ds2024.user_microservice.dto.user.UserRequestDTO;
import org.biamn.ds2024.user_microservice.dto.user.UserResponseDTO;
import org.biamn.ds2024.user_microservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
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

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable("id") UUID userId) {
        return new ResponseEntity<>(
                userService.findById(userId),
                HttpStatus.OK
        );
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> findByEmail(@PathVariable("email") String userEmail) {
        return new ResponseEntity<>(
                userService.findByEmail(userEmail),
                HttpStatus.OK
        );
    }

    @GetMapping("/firstName/{firstName}")
    public ResponseEntity<List<UserResponseDTO>> findByFirstName(@PathVariable("firstName") String firstName) {
        return new ResponseEntity<>(
                userService.findByFirstName(firstName),
                HttpStatus.OK
        );
    }

    @GetMapping("/lastName/{lastName}")
    public ResponseEntity<List<UserResponseDTO>> findByLastName(@PathVariable("lastName") String lastName) {
        return new ResponseEntity<>(
                userService.findByLastName(lastName),
                HttpStatus.OK
        );
    }

    @GetMapping("/paged")
    public ResponseEntity<CollectionResponseDTO<UserResponseDTO>> findAllPaged(@Valid PageRequestDTO page) {
        return new ResponseEntity<>(
                userService.findAllPaged(page),
                HttpStatus.OK
        );
    }

    @PostMapping()
    public ResponseEntity<UserResponseDTO> saveUser(
            @RequestBody @Valid UserRequestDTO userRequestDTO
    )
    {
        return new ResponseEntity<>(
                userService.save(userRequestDTO),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateById(@PathVariable("id") UUID userId, @RequestBody UserRequestDTO userRequestDTO)
    {
        return new ResponseEntity<>(
                userService.updateById(userId, userRequestDTO),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID userId)
    {
        userService.deleteById(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
