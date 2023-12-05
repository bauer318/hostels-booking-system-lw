package ru.bmstu.kibamba.gateway.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ExceptionResponse {
    private String message;
    private int statusCode;
}
