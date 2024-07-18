package com.example.authApi.services;

import com.example.authApi.domain.account.AccountRepository;
import com.example.authApi.domain.account.dtos.RegisterAccountDto;
import com.example.authApi.domain.user.UserRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegisterAccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    public RegisterAccountDto validateAccount(RegisterAccountDto data) {
        if (accountRepository.existsByEmail(data.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email Já foi registrado");
        }
        return data;
    }
}
