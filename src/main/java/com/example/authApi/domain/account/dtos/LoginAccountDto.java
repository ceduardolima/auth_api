package com.example.authApi.domain.account.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginAccountDto(
        @NotNull
        @NotBlank
        @Email
        String email,
        @NotNull
        @NotBlank
        String password
) {
}
