package br.com.wastenio.backend.dto.response;

public record FieldErrorResponse(

        String field,
        String message

) {
}