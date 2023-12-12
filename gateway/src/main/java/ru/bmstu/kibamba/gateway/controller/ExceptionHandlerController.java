package ru.bmstu.kibamba.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.bmstu.kibamba.gateway.exception.ServiceUnavailableException;
import ru.bmstu.kibamba.gateway.payload.ExceptionResponse;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(ServiceUnavailableException.class)
    public ExceptionResponse handleServiceUnavailableException(ServiceUnavailableException exception){
        return new ExceptionResponse(exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value());
    }
}
