package ru.bmstu.kibamba.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.bmstu.kibamba.gateway.exception.ServiceUnavailableException;
import ru.bmstu.kibamba.gateway.payload.ExceptionResponse;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ExceptionResponse handleServiceUnavailableException(ServiceUnavailableException exception){
        return ExceptionResponse
                .builder()
                .message(exception.getMessage())
                .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                .build();
    }
}
