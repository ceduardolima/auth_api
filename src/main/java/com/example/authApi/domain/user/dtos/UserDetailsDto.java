package com.example.authApi.domain.user.dtos;

import com.example.authApi.domain.user.User;

public record UserDetailsDto(
        Long id,
        String name,
        String email
) {
    public UserDetailsDto(User user) {
        this(user.getId(), user.getName(), user.getAccount().getEmail());
    }
}
