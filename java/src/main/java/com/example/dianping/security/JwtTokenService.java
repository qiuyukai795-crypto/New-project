package com.example.dianping.security;

import com.example.dianping.entity.UserAccountEntity;
import com.example.dianping.model.AuthToken;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final AppSecurityProperties securityProperties;

    public JwtTokenService(JwtEncoder jwtEncoder, AppSecurityProperties securityProperties) {
        this.jwtEncoder = jwtEncoder;
        this.securityProperties = securityProperties;
    }

    public AuthToken issueToken(UserAccountEntity user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(securityProperties.getJwt().getExpiresIn());

        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer("dianping-demo")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(String.valueOf(user.getId()))
                .claim("uid", user.getId())
                .claim("name", user.getDisplayName())
                .claim("username", user.getUsername())
                .claim("provider", user.getProvider())
                .claim("role", user.getRole());

        putIfNotBlank(claimsBuilder, "email", user.getEmail());
        putIfNotBlank(claimsBuilder, "avatarUrl", user.getAvatarUrl());

        JwtClaimsSet claims = claimsBuilder.build();

        String token = jwtEncoder.encode(
                JwtEncoderParameters.from(
                        JwsHeader.with(MacAlgorithm.HS256).build(),
                        claims
                )
        ).getTokenValue();

        return new AuthToken(token, expiresAt);
    }

    private void putIfNotBlank(JwtClaimsSet.Builder claimsBuilder, String claimName, String value) {
        if (value != null && !value.isBlank()) {
            claimsBuilder.claim(claimName, value);
        }
    }
}
