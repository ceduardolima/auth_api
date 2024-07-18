package com.example.authApi.domain.account.dtos;

import jakarta.validation.constraints.NotNull;

public record RegisterAccountDto(
        @NotNull
        String email,
        String password,
        String name
) {
}
