package org.biamn.ds2024.device_microservice.exceptions.exception.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
    ERR001_DEVICE_NOT_FOUND("Device with ID %s not found"),
    ERR002_DEVICE_DUPLICATED("Device with ID %s already exists"),
    ERR003_USER_NOT_FOUND("User with ID %s not found")
    ;

    private final String message;
}
