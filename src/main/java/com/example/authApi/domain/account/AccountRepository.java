package com.example.authApi.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);

    Account getReferenceByEmail(String email);

    Boolean existsByEmailAndActiveTrue(String email);

    Account findByEmail(String email);
}
