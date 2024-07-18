package com.example.authApi.domain.account;

import com.example.authApi.domain.account.dtos.RegisterAccountDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Table(name = "account", indexes = @Index(name = "account_email_index", columnList = "email", unique = true))
@Entity(name = "Account")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;

    public Account(RegisterAccountDto data) {
        this.email = data.email();
        this.password = data.password();
    }
}
