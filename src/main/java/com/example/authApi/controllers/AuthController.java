package com.example.authApi.controllers;

import com.example.authApi.domain.account.Account;
import com.example.authApi.domain.account.AccountRepository;
import com.example.authApi.domain.account.dtos.RegisterAccountDto;
import com.example.authApi.domain.user.User;
import com.example.authApi.domain.user.UserRepository;
import com.example.authApi.domain.user.dtos.UserDetailsDto;
import com.example.authApi.services.RegisterAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid  RegisterAccountDto data, UriComponentsBuilder uriComponentsBuilder) {
        boolean emailAlreadyExists = accountRepository.existsByEmail(data.email());
        if (emailAlreadyExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email já existe");
        }
        final Account account = new Account(data);
        final Account savedAccount = accountRepository.save(account);
        final User user = new User(savedAccount, data);
        final User savedUser = userRepository.save(user);
        var uri = uriComponentsBuilder.path("/user/{id}").buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(uri).body(new UserDetailsDto(savedUser));
    }
}
