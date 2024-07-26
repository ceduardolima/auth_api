package com.example.authApi.domain.account.validators;

import com.example.authApi.domain.account.dtos.RegisterAccountDto;

public interface RegisterValidator {
    void validate(RegisterAccountDto data);
}
