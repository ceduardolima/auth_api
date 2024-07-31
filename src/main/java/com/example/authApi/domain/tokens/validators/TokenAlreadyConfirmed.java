package com.example.authApi.domain.tokens.validators;

import com.example.authApi.domain.tokens.EmailConfirmationToken;
import com.example.authApi.domain.tokens.EmailConfirmationTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class TokenAlreadyConfirmed implements ConfirmTokenValidator {
    @Override
    public void validate(EmailConfirmationToken token) {
        if(token.isConfirmed())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token already confirmed");
    }
}
