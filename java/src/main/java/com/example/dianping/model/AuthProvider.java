package com.example.dianping.model;

public record AuthProvider(
        String registrationId,
        String clientName,
        String authorizationUrl
) {
}
