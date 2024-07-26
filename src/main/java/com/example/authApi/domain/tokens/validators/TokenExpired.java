package com.example.authApi.domain.tokens.validators;

import com.example.authApi.domain.tokens.EmailConfirmationToken;
import com.example.authApi.domain.tokens.EmailConfirmationTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TokenExpired implements ConfirmTokenValidator {
    private EmailConfirmationTokenRepository emailConfirmationTokenRepository;
    @Override
    public void validate(EmailConfirmationToken token) {
        if (token.isExpired())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token already expired");
    }
}
