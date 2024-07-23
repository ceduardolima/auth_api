package com.example.authApi.domain.errors;

public record ErrorDto(
        int status,
        String message
) {
}
