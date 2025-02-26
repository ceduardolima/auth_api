package com.example.authApi.domain.user;

import com.example.authApi.domain.account.Account;
import com.example.authApi.domain.account.dtos.RegisterAccountDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "tb_user")
@Entity(name = "User")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    public User(Account account, RegisterAccountDto data) {
        this.account = account;
        this.name = data.name();
    }
}
