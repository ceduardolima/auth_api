package com.example.authApi.services;

import com.example.authApi.domain.account.Account;
import com.example.authApi.domain.account.AccountRepository;
import com.example.authApi.domain.account.dtos.LoginAccountDto;
import com.example.authApi.domain.account.dtos.RegisterAccountDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.AuthenticationException;

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
        var registerData = createRegisterAccountDt("new-email@email.com");
        var registerAccount = authService.registerAccount(registerData);
        assertThat(registerAccount.getId()).isNotNull();
        accountRepository.deleteById(registerAccount.getId());
    }

    @Test
    void registerAlreadyExistingAccount_return_httpsStatusConflict() {
        var registerData = createRegisterAccountDt("email@email.com");
        var result = assertThrows(ResponseStatusException.class, () -> {
            authService.registerAccount(registerData);
        });
        assertThat(result).isInstanceOf(ResponseStatusException.class);
    }

    RegisterAccountDto createRegisterAccountDt(String email) {
        return new RegisterAccountDto(email, "123456", "name");
    }

    @Test
    void authenticateNonExistingEmail_return_httpStatus404() {
        final var loginDto = new LoginAccountDto("wrong-email@email.com", "123456");
        var result = assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(loginDto);
        });
        assertThat(result).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void authenticateWrongPassword_return_httpStatus404() {
        final var loginDto = new LoginAccountDto("email@email.com", "wrong-pass");
        var result = assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(loginDto);
        });
        assertThat(result).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void authenticateExistingUser_return_httpStatusOK() {
        final var loginDto = new LoginAccountDto("email@email.com", "123456");
        final var token = authService.authenticate(loginDto);
        assertThat(token).isNotNull();
    }

    @Test
    void validateExistingAccount_return_account() {
        final var loginDto = new LoginAccountDto("email@email.com", "123456");
        final var account = authService.validateExistingAccount(loginDto);
        assertThat(account).isNotNull();
        assertThat(account.getEmail()).isEqualTo(loginDto.email());
        assertThat(account.getPassword()).isNotEqualTo(loginDto.email());
    }

    @Test
    void validateNonExistingEmail_return_null() {
        final var loginDto = new LoginAccountDto("wrong@email.com", "123456");
        final var account = authService.validateExistingAccount(loginDto);
        assertThat(account).isNull();
    }

    @Test
    void validateWrongPassword_return_account() {
        final var loginDto = new LoginAccountDto("email@email.com", "wrong-pass");
        final var account = authService.validateExistingAccount(loginDto);
        assertThat(account).isNull();
    }
}