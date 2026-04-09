package com.example.dianping.model;

import java.time.Instant;

public record AuthToken(
        String accessToken,
        Instant expiresAt
) {
}
