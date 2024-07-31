package com.example.authApi.domain.tokens.validators;

import com.example.authApi.domain.account.Account;
import com.example.authApi.domain.tokens.EmailConfirmationToken;
import com.example.authApi.domain.tokens.EmailConfirmationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenAlreadyConfirmedTest {

    @InjectMocks
    TokenAlreadyConfirmed tokenAlreadyConfirmed;

    @Test
    void tokenAlreadyConfirmed_Return_httpStatus_BadRequest() {
        final var token = createEmailConfirmationToken(true);
        final var exception = assertThrows(ResponseStatusException.class, () -> {
            tokenAlreadyConfirmed.validate(token);
        });
        assertThat(exception.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void tokenNotConfirmed_Return_Nothing() {
        final var token = createEmailConfirmationToken(false);
        tokenAlreadyConfirmed.validate(token);
    }

    EmailConfirmationToken createEmailConfirmationToken(boolean isConfirmed) {
        var now = LocalDateTime.now();
        var account = new Account("email@email.com", "123456", true);
        return new EmailConfirmationToken(1L, "token", now.minusMinutes(15), now.minusMinutes(1), isConfirmed ? now.minusMinutes(2) : null, account);
    }
}