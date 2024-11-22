package org.biamn.ds2024.user_microservice.exceptions.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
    ERR001_USER_NOT_FOUND("User with ID %s not found"),
    ERR002_USER_ALREADY_EXISTS("User with email %s already exists"),
    ERR003_DEVICE_MICROSERVICE_DOWN("Device microservice is down")

    ;

    private final String message;
}
