package com.example.authApi.domain.tokens;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken, Long> {
    Optional<EmailConfirmationToken> findByToken(String token);

    Optional<EmailConfirmationToken> findByAccountEmail(String email);

    void deleteByAccountId(Long accountId);
}
