package com.example.dianping.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {

    private String frontendBaseUrl = "http://localhost:5173";
    private final Jwt jwt = new Jwt();
    private final Providers providers = new Providers();

    public String getFrontendBaseUrl() {
        return frontendBaseUrl;
    }

    public void setFrontendBaseUrl(String frontendBaseUrl) {
        this.frontendBaseUrl = frontendBaseUrl;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public Providers getProviders() {
        return providers;
    }

    public static class Providers {
        private final Github github = new Github();
        private final Local local = new Local();

        public Github getGithub() {
            return github;
        }

        public Local getLocal() {
            return local;
        }
    }

    public static class Github {
        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class Local {
        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
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
