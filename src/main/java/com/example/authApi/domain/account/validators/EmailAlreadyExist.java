package com.example.authApi.domain.account.validators;

import com.example.authApi.domain.account.AccountRepository;
import com.example.authApi.domain.account.dtos.RegisterAccountDto;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class EmailAlreadyExist implements RegisterValidator {
    AccountRepository accountRepository;
    @Override
    public void validate(RegisterAccountDto data) {
        boolean emailAlreadyExists = accountRepository.existsByEmail(data.email());
        if (emailAlreadyExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
    }
}
