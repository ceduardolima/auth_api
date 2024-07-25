package com.example.authApi.services;

import com.example.authApi.domain.account.Account;
import com.example.authApi.domain.account.AccountRepository;
import com.example.authApi.domain.account.dtos.LoginAccountDto;
import com.example.authApi.domain.tokens.EmailConfirmationToken;
import com.example.authApi.domain.tokens.EmailConfirmationTokenRepository;
import com.example.authApi.domain.user.User;
import com.example.authApi.domain.user.UserRepository;
import com.example.authApi.utils.EmailTemplates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailConfirmationTokenService {
    static private final Logger log = LoggerFactory.getLogger(EmailConfirmationTokenService.class);
    static private final String CONFIRM_EMAIL_LINK = "http://localhost:8080/auth/confirmToken?token=%s";
    @Autowired
    private EmailConfirmationTokenRepository emailConfirmationTokenRepository;
    @Value("${api.security.token.emailValidation}")
    private Integer expiration;
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

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
    public void confirmToken(String token) {
        var confirmationToken = emailConfirmationTokenRepository.findByToken(token).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid token"));
        if (confirmationToken.isExpired())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token already expired");
        else if (confirmationToken.isConfirmed())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token already confirmed");
        confirmationToken.confirmToken();
        Account account = confirmationToken.getAccount();
        account.setActive(true);
    }

    @Transactional
    public void resendTokenIfIsInvalid(Account account) {
        Boolean isValid = isTokenValid(account.getEmail());
        if (isValid) return;
        emailConfirmationTokenRepository.deleteByAccountId(account.getId());
        User user = userRepository.getReferenceByAccountId(account.getId());
        EmailConfirmationToken newToken = createToken(account);
        sendConfirmationToken(account.getEmail(), user.getName(), newToken.getToken());
    }

    public void sendConfirmationToken(String to, String name, String token) {
        try {
            String link = String.format(CONFIRM_EMAIL_LINK, token);
            String template = EmailTemplates.confirmToken(name, link);
            emailService.sendSimpleEmail(to, template);
        } catch (RuntimeException e) {
            log.error("Send email error!");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error to send the confirmation email");
        }
    }

    public Boolean isTokenValid(String email) {
        var token = emailConfirmationTokenRepository.findByAccountEmail(email);
        return token.filter(
                emailConfirmationToken ->
                        !(emailConfirmationToken.isExpired() || emailConfirmationToken.isConfirmed())
        ).isPresent();
    }
}
