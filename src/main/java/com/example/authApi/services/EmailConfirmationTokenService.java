package com.example.authApi.services;

import com.example.authApi.domain.account.Account;
import com.example.authApi.domain.tokens.EmailConfirmationToken;
import com.example.authApi.domain.tokens.EmailConfirmationTokenRepository;
import com.example.authApi.utils.EmailTemplates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailConfirmationTokenService {
    @Autowired
    private EmailConfirmationTokenRepository emailConfirmationTokenRepository;
    @Value("${api.security.token.emailValidation}")
    private Integer expiration;
    @Autowired
    private EmailServiceImpl emailService;

    public void saveConfirmationToken(EmailConfirmationToken token) {
        emailConfirmationTokenRepository.save(token);
    }

    @Transactional
    public EmailConfirmationToken createToken(Account account) {
        String token = UUID.randomUUID().toString();
        EmailConfirmationToken confirmationToken = new EmailConfirmationToken(
                null,
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(expiration),
                null,
                account
        );
        emailConfirmationTokenRepository.save(confirmationToken);
        return confirmationToken;
    }

    @Transactional
    public EmailConfirmationToken confirmToken(String token) {
        var confirmationToken = emailConfirmationTokenRepository.findByToken(token).orElseThrow(() -> new IllegalStateException("Token não foi emitido"));
        if (confirmationToken.isExpired())
            throw new IllegalStateException("Token expirado");
        else if (confirmationToken.isConfirmed())
            throw new IllegalStateException("Token já foi confirmado");
        confirmationToken.confirmToken();
        Account account = confirmationToken.getAccount();
        account.setActive(true);
        return confirmationToken;
    }

    public void sendConfirmationToken(String to, String name, String token) {
        String link = "http://localhost:8080/auth/confirmToken?token=" + token;
        String template = EmailTemplates.confirmToken(name, link);
        emailService.sendSimpleEmail(to, template);
    }
}
