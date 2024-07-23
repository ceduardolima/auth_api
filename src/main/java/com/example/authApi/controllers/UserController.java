package com.example.authApi.controllers;

import com.example.authApi.domain.account.Account;
import com.example.authApi.domain.account.AccountRepository;
import com.example.authApi.domain.account.dtos.LoginAccountDto;
import com.example.authApi.domain.user.User;
import com.example.authApi.domain.user.UserRepository;
import com.example.authApi.domain.user.dtos.UserDetailsDto;
import com.example.authApi.infra.security.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/{id}")
    public ResponseEntity detail(@PathVariable Long id) {
        User user = userRepository.getReferenceById(id);
        return ResponseEntity.ok(new UserDetailsDto(user));
    }

    @PostMapping("/sendEmail")
    public ResponseEntity sendEmailValidation(@RequestHeader("Authorization") String token, @RequestBody LoginAccountDto data) {
        String tokenJWT = tokenService.getTokenJWT(token);
        String tokenSubject = tokenService.getSubject(tokenJWT);
        boolean emailIsValid = tokenSubject.equals(data.email());
        if (emailIsValid) {
            Account account = accountRepository.getReferenceByEmail(data.email());
            if (encoder.matches(data.password(), account.getPassword()))
                return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
