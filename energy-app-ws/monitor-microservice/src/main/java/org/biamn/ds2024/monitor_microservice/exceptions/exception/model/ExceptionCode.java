package org.biamn.ds2024.monitor_microservice.exceptions.exception.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {
    ERR001_DEVICE_NOT_FOUND("Device with ID %s not found"),
    ERR002_DEVICE_DUPLICATED("Device with ID %s already exists"),
    ERR003_USER_NOT_FOUND("User with ID %s not found"),
    ERR004_MONITOR_DUPLICATED("Monitor for device with ID %s already exists")
    ;

    private final String message;
}
