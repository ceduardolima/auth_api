package com.example.authApi.domain.tokens.validators;

import com.example.authApi.domain.account.Account;
import com.example.authApi.domain.tokens.EmailConfirmationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenExpiredTest {
    @InjectMocks
    TokenExpired tokenExpired;

    @Test
    void tokenAlreadyExpired_return_httpStatus_badRequest() {
        final var token = createEmailConfirmationToken(true);
        final var exception = assertThrows(ResponseStatusException.class, () -> {
            tokenExpired.validate(token);
        });
        assertThat(exception.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void tokenNotExpired_return_nothing() {
        final var token = createEmailConfirmationToken(false);
        tokenExpired.validate(token);
    }

    EmailConfirmationToken createEmailConfirmationToken(boolean isExpired) {
        var now = LocalDateTime.now();
        var account = new Account("email@email.com", "123456", true);
        return new EmailConfirmationToken(1L, "token", now.minusMinutes(15), isExpired ? now.minusMinutes(1) : now.plusMinutes(1), null, account);
    }
}