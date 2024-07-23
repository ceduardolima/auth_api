package com.example.authApi.infra.errors;

import com.example.authApi.domain.errors.EmailNotVerifiedException;
import com.example.authApi.domain.errors.ErrorDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity handleError404() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleError400() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ErrorDto> handleErrorEmailNotValidated(EmailNotVerifiedException e) {
        return createErrorDto(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    private ResponseEntity<ErrorDto> createErrorDto(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorDto(status.value(), message));
    }
}
