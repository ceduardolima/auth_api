package com.example.authApi.services;

import com.example.authApi.domain.account.AccountRepository;
import com.example.authApi.domain.account.dtos.RegisterAccountDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integrationtest")
class AuthServiceTest {

    private final AuthService authService;
    private final AccountRepository accountRepository;

    @Autowired
    public AuthServiceTest(AuthService authService, AccountRepository accountRepository) {
        this.authService = authService;
        this.accountRepository = accountRepository;
    }

    @Test
    void registerNewAccount() {
        var registerData = new RegisterAccountDto("new-email@email.com", "123456", "name");
        var registerAccount = authService.registerAccount(registerData);
        assertThat(registerAccount.getId()).isNotNull();
        accountRepository.deleteById(registerAccount.getId());
    }

    @Test
    void registerAlreadyExistingAccount_return_httpsStatusConflict() {
        var registerData = new RegisterAccountDto("email@email.com", "123456", "name");
        var result = assertThrows(ResponseStatusException.class, () -> {
            authService.registerAccount(registerData);
        });
        assertThat(result).isInstanceOf(ResponseStatusException.class);
    }

}