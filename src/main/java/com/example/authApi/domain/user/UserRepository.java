package com.example.authApi.domain.user;

import com.example.authApi.domain.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User getReferenceByAccountId(Long accountId);
}
