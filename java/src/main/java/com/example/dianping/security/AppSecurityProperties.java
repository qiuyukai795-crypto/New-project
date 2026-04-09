package com.example.dianping.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {

    private String frontendBaseUrl = "http://localhost:5173";
    private final Jwt jwt = new Jwt();

    public String getFrontendBaseUrl() {
        return frontendBaseUrl;
    }

    public void setFrontendBaseUrl(String frontendBaseUrl) {
        this.frontendBaseUrl = frontendBaseUrl;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public static class Jwt {
        private String secret = "change-this-demo-secret-change-this-demo-secret-2026";
        private Duration expiresIn = Duration.ofHours(2);

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public Duration getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(Duration expiresIn) {
            this.expiresIn = expiresIn;
        }
    }
}
