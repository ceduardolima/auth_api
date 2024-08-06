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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    static private Logger log = LoggerFactory.getLogger(AuthController.class);
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

    @ApiResponses(value = {
            @ApiResponse(responseCode = "409", description = "Email already exist", content = @Content),
            @ApiResponse(responseCode = "200", description = "Register account and send a email validation", content = @Content),
    })
    @PostMapping("/register")
    @Transactional
    public ResponseEntity register(@RequestBody @Valid RegisterAccountDto data) {
        Account account = authService.registerAccount(data);
        EmailConfirmationToken confirmationToken = emailConfirmationTokenService.createToken(account);
        emailConfirmationTokenService.sendConfirmationToken(data.email(), data.name(), confirmationToken.getToken());
        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Account not founded", content = @Content),
            @ApiResponse(responseCode = "200", description = "Return token"),
    })
    @PostMapping("/login")
    @Transactional
    public ResponseEntity<TokenJWTDto> login(@RequestBody @Valid LoginAccountDto data) {
        User user = userRepository.findByAccountEmail(data.email()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        String tokenJWT = authService.authenticate(data);
        return ResponseEntity.ok(new TokenJWTDto(user, tokenJWT));
    }

    @Operation(summary = "Validate an account created recently")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Invalid token", content = @Content),
            @ApiResponse(responseCode = "200", description = "Enable account access", content = @Content),
    })
    @GetMapping("/confirmToken")
    @Transactional
    public ResponseEntity validateEmail(@Parameter(description = "Email validation token") @RequestParam(value = "token") String token) {
        emailConfirmationTokenService.confirmToken(token);
        return ResponseEntity.ok("confirmed");
    }

    @Operation(summary = "Resend the email validation token for account validation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Send the email if account exists", content = @Content),
    })
    @PostMapping("/resendToken")
    @Transactional
    public ResponseEntity resendToken(@RequestBody @Valid LoginAccountDto data) {
        Account account = authService.validateExistingAccount(data);
        if (account == null) return ResponseEntity.ok().build();
        emailConfirmationTokenService.resendTokenIfIsInvalid(account);
        return ResponseEntity.ok().build();
    }
}
