package com.example.authApi.infra.security;

import com.example.authApi.domain.user.User;

public record TokenJWTDto(
        Long id,
        String name,
        String email,
        String token
) {
    public TokenJWTDto(User user, String token) {
        this(user.getId(), user.getName(), user.getAccount().getEmail(), token);
    }
}
