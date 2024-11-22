package org.biamn.ds2024.device_microservice.exceptions;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.biamn.ds2024.device_microservice.exceptions.exception.model.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        Set<ConstraintViolation<?>> details = e.getConstraintViolations();
        ExceptionResponseDTO errorInformation = new ExceptionResponseDTO(e.getMessage(),
                status.getReasonPhrase(),
                status.value(),
                e.getMessage(),
                details,
                request.getDescription(false));
        return handleExceptionInternal(
                e,
                errorInformation,
                new HttpHeaders(),
                status,
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ObjectError> errs = ex.getBindingResult().getAllErrors();
        List<String> details = new ArrayList<>();
        for (ObjectError err : errs) {
            String fieldName = ((FieldError) err).getField();
            String errorMessage = err.getDefaultMessage();
            details.add(fieldName + ":" + errorMessage);
        }
        ExceptionResponseDTO errorInformation = new ExceptionResponseDTO(ex.getParameter().getParameterName(),
                status.toString(),
                status.value(),
                MethodArgumentNotValidException.class.getSimpleName(),
                details,
                request.getDescription(false));
        return handleExceptionInternal(
                ex,
                errorInformation,
                new HttpHeaders(),
                status,
                request
        );
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class, DuplicateResourceException.class, EntityValidationException.class})
    protected ResponseEntity<Object> handleResourceNotFound(CustomException ex,
                                                            WebRequest request) {
        ExceptionResponseDTO errorInformation = new ExceptionResponseDTO(ex.getResource(),
                ex.getStatus().getReasonPhrase(),
                ex.getStatus().value(),
                ex.getMessage(),
                ex.getValidationErrors(),
                request.getDescription(false));
        return handleExceptionInternal(
                ex,
                errorInformation,
                new HttpHeaders(),
                ex.getStatus(),
                request
        );
    }

    @ExceptionHandler(value={RuntimeException.class})
    public ResponseEntity<Object> handleException(RuntimeException exp, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ExceptionResponseDTO errorInformation = new ExceptionResponseDTO(exp.getMessage(),
                status.getReasonPhrase(),
                status.value(),
                exp.getMessage(),
                null,
                request.getDescription(false));
        return handleExceptionInternal(
                exp,
                errorInformation,
                new HttpHeaders(),
                status,
                request
        );
    }

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<Object> handleGlobalException(Exception exp, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ExceptionResponseDTO errorInformation = new ExceptionResponseDTO(
                exp.getMessage(),
                status.getReasonPhrase(),
                status.value(),
                exp.getMessage(),
                null,
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorInformation, new HttpHeaders(), status);
    }
}
