package com.example.authApi.services;

import com.example.authApi.domain.account.Account;
import com.example.authApi.domain.account.AccountRepository;
import com.example.authApi.domain.account.dtos.LoginAccountDto;
import com.example.authApi.domain.account.dtos.RegisterAccountDto;
import com.example.authApi.domain.user.User;
import com.example.authApi.domain.user.UserRepository;
import com.example.authApi.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private TokenService tokenService;

    @Transactional
    public Account registerAccount(RegisterAccountDto data) {
        validateExistingAccount(data.email());
        Account account = createAndSaveAccount(data.email(), data.password());
        final Account savedAccount = accountRepository.save(account);
        final User user = new User(savedAccount, data);
        userRepository.save(user);
        return account;
    }

    public void validateExistingAccount(String email) {
        boolean emailAlreadyExists = accountRepository.existsByEmail(email);
        if (emailAlreadyExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
    }

    public Account createAndSaveAccount(String email, String password) {
        final String encodedPassword = encoder.encode(password);
        final Account account = new Account(email, encodedPassword, false);
        accountRepository.save(account);
        return account;
    }

    @Transactional
    public String authenticate(LoginAccountDto data) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        Authentication auth = manager.authenticate(authToken);
        return tokenService.genToken((Account) auth.getPrincipal());
    }

    @Transactional
    public Account validateExistingAccount(LoginAccountDto data) {
        final Optional<Account> accountOptional = accountRepository.findByEmail(data.email());
        if (accountOptional.isEmpty()) return null;
        final boolean passwordMatched = encoder.matches(data.password(), accountOptional.get().getPassword());
        return passwordMatched ? accountOptional.get() : null;
    }
}
