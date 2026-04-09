package com.example.dianping.model;

public record AuthUser(
        Long id,
        String displayName,
        String username,
        String email,
        String avatarUrl,
        String provider,
        String role
) {
}
