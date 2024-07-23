package com.example.authApi.controllers;

import com.example.authApi.domain.account.Account;
import com.example.authApi.domain.account.AccountRepository;
import com.example.authApi.domain.account.dtos.LoginAccountDto;
import com.example.authApi.domain.account.dtos.RegisterAccountDto;
import com.example.authApi.domain.tokens.EmailConfirmationToken;
import com.example.authApi.domain.user.User;
import com.example.authApi.domain.user.UserRepository;
import com.example.authApi.domain.user.dtos.UserDetailsDto;
import com.example.authApi.infra.security.TokenJWTDto;
import com.example.authApi.infra.security.TokenService;
import com.example.authApi.services.AuthService;
import com.example.authApi.services.EmailConfirmationTokenService;
import com.example.authApi.services.EmailServiceImpl;
import com.example.authApi.services.RegisterAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private EmailConfirmationTokenService emailConfirmationTokenService;
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterAccountDto data, UriComponentsBuilder uriComponentsBuilder) {
        Account account = authService.registerAccount(data);
        EmailConfirmationToken confirmationToken = emailConfirmationTokenService.createToken(account);
        emailConfirmationTokenService.saveConfirmationToken(confirmationToken);
        return ResponseEntity.ok(confirmationToken.getToken());
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginAccountDto data) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        Authentication auth = manager.authenticate(authToken);
        String tokenJWT = tokenService.genToken((Account) auth.getPrincipal());
        return ResponseEntity.ok(new TokenJWTDto(tokenJWT));
    }
    @PostMapping("/sendEmail")
    public ResponseEntity sendEmailValidation(@RequestHeader("Authorization") String token, @RequestBody LoginAccountDto data) {
        String tokenJWT = tokenService.getTokenJWT(token);
        String tokenSubject = tokenService.getSubject(tokenJWT);
        boolean emailIsValid = tokenSubject.equals(data.email());
        if (emailIsValid) {
            Account account = accountRepository.getReferenceByEmail(data.email());
            if (encoder.matches(data.password(), account.getPassword())) {
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
