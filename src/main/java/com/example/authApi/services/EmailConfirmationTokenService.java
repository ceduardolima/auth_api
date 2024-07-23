package com.example.authApi.services;

import com.example.authApi.domain.account.Account;
import com.example.authApi.domain.tokens.EmailConfirmationToken;
import com.example.authApi.domain.tokens.EmailConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailConfirmationTokenService {
    @Autowired
    private EmailConfirmationTokenRepository emailConfirmationTokenRepository;
    @Value("${api.security.token.emailValidation}")
    private Integer expiration;

    public void saveConfirmationToken(EmailConfirmationToken token) {
        emailConfirmationTokenRepository.save(token);
    }

    public EmailConfirmationToken createToken(Account account) {
        String token = UUID.randomUUID().toString();
        return new EmailConfirmationToken(
                null,
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(expiration),
                null,
                account
        );
    }
}
