package com.example.authApi.services;

import com.example.authApi.domain.account.Account;
import com.example.authApi.domain.account.AccountRepository;
import com.example.authApi.domain.account.dtos.RegisterAccountDto;
import com.example.authApi.domain.tokens.EmailConfirmationToken;
import com.example.authApi.domain.user.User;
import com.example.authApi.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {
    private final static String USER_NOT_FOUND_MSG = "User with email %s not found";
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
    }

    public Account registerAccount(RegisterAccountDto data) {
        validateAccount(data.email());
        Account account = createAndSaveAccount(data.email(), data.password());
        final Account savedAccount = accountRepository.save(account);
        final User user = new User(savedAccount, data);
        userRepository.save(user);
        return account;
    }

    public void validateAccount(String email) {
        boolean emailAlreadyExists = accountRepository.existsByEmail(email);
        if (emailAlreadyExists) {
            throw new RuntimeException("Email already exists");
        }
    }

    public Account createAndSaveAccount(String email, String password) {
        final String encodedPassword = encoder.encode(password);
        final Account account = new Account(email, encodedPassword, false);
        accountRepository.save(account);
        return account;
    }
}
