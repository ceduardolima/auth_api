package com.example.authApi.services;

import com.example.authApi.domain.account.Account;
import com.example.authApi.domain.account.AccountRepository;
import com.example.authApi.domain.account.dtos.LoginAccountDto;
import com.example.authApi.domain.account.dtos.RegisterAccountDto;
import com.example.authApi.domain.account.validators.RegisterValidator;
import com.example.authApi.domain.user.User;
import com.example.authApi.domain.user.UserRepository;
import com.example.authApi.infra.security.TokenService;
import com.example.authApi.services.interfaces.IAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService implements IAuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
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
    @Autowired
    private List<RegisterValidator> validators;

    @Override
    public Account registerAccount(RegisterAccountDto data) {
        validators.forEach(v -> v.validate(data));
        Account account = createAndSaveAccount(data.email(), data.password());
        final User user = new User(account, data);
        userRepository.save(user);
        return account;
    }

    private Account createAndSaveAccount(String email, String password) {
        final String encodedPassword = encoder.encode(password);
        final Account account = new Account(email, encodedPassword, false);
        return accountRepository.save(account);
    }

    @Override
    public String authenticate(LoginAccountDto data) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        Authentication auth = manager.authenticate(authToken);
        return tokenService.genToken((Account) auth.getPrincipal());
    }

    @Override
    public Account validateExistingAccount(LoginAccountDto data) {
        final Optional<Account> accountOptional = accountRepository.findByEmail(data.email());
        if (accountOptional.isEmpty()) return null;
        final boolean passwordMatched = encoder.matches(data.password(), accountOptional.get().getPassword());
        return passwordMatched ? accountOptional.get() : null;
    }
}
