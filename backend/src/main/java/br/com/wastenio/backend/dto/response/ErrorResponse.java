package br.com.wastenio.backend.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(

        LocalDateTime timestamp,
        Integer status,
        String error,
        String message,
        String path,
        List<FieldErrorResponse> fieldErrors

) {
}