package org.biamn.ds2024.monitor_microservice.exceptions.exception.model;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ParameterValidationException extends CustomException {
    private static final String MESSAGE = "Parameter is invalid!";
    private static final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public ParameterValidationException(String resource, List<String> errors) {
        super(MESSAGE, httpStatus, resource, errors);
    }
}