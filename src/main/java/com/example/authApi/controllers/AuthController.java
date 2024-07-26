package com.example.authApi.controllers;

import com.example.authApi.domain.account.Account;
import com.example.authApi.domain.account.AccountRepository;
import com.example.authApi.domain.account.dtos.LoginAccountDto;
import com.example.authApi.domain.account.dtos.RegisterAccountDto;
import com.example.authApi.domain.tokens.EmailConfirmationToken;
import com.example.authApi.domain.user.User;
import com.example.authApi.domain.user.UserRepository;
import com.example.authApi.infra.security.TokenJWTDto;
import com.example.authApi.services.AuthService;
import com.example.authApi.services.EmailConfirmationTokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    static  private Logger log = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private EmailConfirmationTokenService emailConfirmationTokenService;
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterAccountDto data) {
        Account account = authService.registerAccount(data);
        EmailConfirmationToken confirmationToken = emailConfirmationTokenService.createToken(account);
        emailConfirmationTokenService.sendConfirmationToken(data.email(), data.name(), confirmationToken.getToken());
        return ResponseEntity.ok(confirmationToken.getToken());
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginAccountDto data) {
        String tokenJWT = authService.authenticate(data);
        return ResponseEntity.ok(new TokenJWTDto(tokenJWT));
    }

    @GetMapping("/confirmToken")
    public ResponseEntity validateEmail(@RequestParam(value = "token") String token) {
        emailConfirmationTokenService.confirmToken(token);
        return ResponseEntity.ok("confirmed");
    }

    @PostMapping("/resendToken")
    public ResponseEntity resendToken(@RequestBody @Valid LoginAccountDto data) {
        Account account = authService.validateExistingAccount(data);
        if (account == null) return ResponseEntity.ok().build();
        emailConfirmationTokenService.resendTokenIfIsInvalid(account);
        return ResponseEntity.ok().build();
    }
}
