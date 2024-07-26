package com.example.authApi.domain.tokens.validators;

import com.example.authApi.domain.tokens.EmailConfirmationToken;

public interface ConfirmTokenValidator {
    void validate(EmailConfirmationToken token);
}
