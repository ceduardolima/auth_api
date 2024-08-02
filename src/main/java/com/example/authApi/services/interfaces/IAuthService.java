package com.example.authApi.services.interfaces;

import com.example.authApi.domain.account.Account;
import com.example.authApi.domain.account.dtos.LoginAccountDto;
import com.example.authApi.domain.account.dtos.RegisterAccountDto;
import com.example.authApi.domain.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface IAuthService {

    Account registerAccount(RegisterAccountDto data);

    String authenticate(LoginAccountDto data);

    Account validateExistingAccount(LoginAccountDto data);
}
