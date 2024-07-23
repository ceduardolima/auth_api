package com.example.authApi.domain.tokens;

import com.example.authApi.domain.account.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_email_confirmation_token")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class EmailConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String token;
    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;
    @Column(name="expires_at", nullable = false)
    private LocalDateTime expiresAt;
    @Column(name="confirmed_at")
    private LocalDateTime confirmedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    public Boolean isExpired() {
        System.out.println("now: " + LocalDateTime.now());
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public Boolean isConfirmed() {
        return confirmedAt != null;
    }

    public void confirmToken() {
        this.setConfirmedAt(LocalDateTime.now());
    }
}
