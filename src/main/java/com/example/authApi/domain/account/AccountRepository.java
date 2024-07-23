package com.example.authApi.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);

    Account getReferenceByEmail(String email);

    Boolean existsByEmailAndActiveTrue(String email);

    Optional<Account> findByEmail(String email);
    Boolean existsByEmailAndPassword(String email, String password);
}
