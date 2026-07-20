package br.com.wastenio.backend.handler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.wastenio.backend.dto.response.ErrorResponse;
import br.com.wastenio.backend.dto.response.FieldErrorResponse;
import br.com.wastenio.backend.exception.BusinessException;
import br.com.wastenio.backend.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<FieldErrorResponse> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldErrorResponse)
                .toList();

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                "Validation Error",
                "One or more fields are invalid",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "A database integrity violation occurred",
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception exception,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "An unexpected internal server error occurred",
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(status).body(response);
    }

    private FieldErrorResponse toFieldErrorResponse(FieldError fieldError) {
        return new FieldErrorResponse(
                fieldError.getField(),
                fieldError.getDefaultMessage()
        );
    }
}