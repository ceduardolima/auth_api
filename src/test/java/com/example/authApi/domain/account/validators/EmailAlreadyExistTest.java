package com.example.authApi.domain.account.validators;

import com.example.authApi.domain.account.AccountRepository;
import com.example.authApi.domain.account.dtos.RegisterAccountDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailAlreadyExistTest {
    @Mock
    AccountRepository accountRepository;
    @InjectMocks
    EmailAlreadyExist emailAlreadyExist;

    @Test()
    void emailNotExist_returnVoid() {
        final RegisterAccountDto data = createData("email-not-exist@email.com");
        when(accountRepository.existsByEmail(data.email())).thenReturn(false);
        emailAlreadyExist.validate(data);
    }

    @Test()
    void emailAlreadyExist_HttpStatusConflict() {
        final RegisterAccountDto data = createData("email-already-exist@email.com");
        when(accountRepository.existsByEmail(data.email())).thenReturn(true);
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            emailAlreadyExist.validate(data);
        });
        assertThat(exception.getStatusCode().value()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    private RegisterAccountDto createData(String email) {
        return new RegisterAccountDto(email, "password", "name");
    }
}