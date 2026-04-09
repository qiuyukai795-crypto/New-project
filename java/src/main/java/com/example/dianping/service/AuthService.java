package com.example.dianping.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.dianping.entity.UserAccountEntity;
import com.example.dianping.model.AuthProvider;
import com.example.dianping.model.AuthToken;
import com.example.dianping.model.AuthUser;
import com.example.dianping.security.JwtTokenService;
import com.example.dianping.service.db.UserAccountDbService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Service
public class AuthService {

    private final UserAccountDbService userAccountDbService;
    private final JwtTokenService jwtTokenService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public AuthService(UserAccountDbService userAccountDbService,
                       JwtTokenService jwtTokenService,
                       ObjectProvider<ClientRegistrationRepository> clientRegistrationRepositoryProvider) {
        this.userAccountDbService = userAccountDbService;
        this.jwtTokenService = jwtTokenService;
        this.clientRegistrationRepository = clientRegistrationRepositoryProvider.getIfAvailable();
    }

    @Transactional
    public AuthToken handleOAuth2Login(OAuth2AuthenticationToken authenticationToken) {
        String registrationId = authenticationToken.getAuthorizedClientRegistrationId();
        OAuth2User oauth2User = authenticationToken.getPrincipal();
        String providerUserId = extractProviderUserId(oauth2User);
        String username = extractUsername(oauth2User, providerUserId);
        String displayName = extractDisplayName(oauth2User, username);
        String email = firstNonBlankValue(oauth2User.getAttribute("email"));
        String avatarUrl = firstNonBlankValues(oauth2User.getAttribute("avatar_url"), oauth2User.getAttribute("picture"));
        LocalDateTime now = LocalDateTime.now();

        UserAccountEntity user = userAccountDbService.getOne(Wrappers.<UserAccountEntity>lambdaQuery()
                .eq(UserAccountEntity::getProvider, registrationId)
                .eq(UserAccountEntity::getProviderUserId, providerUserId));

        if (user == null) {
            user = new UserAccountEntity();
            user.setProvider(registrationId);
            user.setProviderUserId(providerUserId);
            user.setCreatedAt(now);
            user.setRole("ROLE_USER");
        }

        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setEmail(email);
        user.setAvatarUrl(avatarUrl);
        user.setUpdatedAt(now);
        user.setLastLoginAt(now);

        userAccountDbService.saveOrUpdate(user);
        if (user.getId() == null) {
            user = userAccountDbService.getOne(Wrappers.<UserAccountEntity>lambdaQuery()
                    .eq(UserAccountEntity::getProvider, registrationId)
                    .eq(UserAccountEntity::getProviderUserId, providerUserId));
        }

        if (user == null || user.getId() == null) {
            throw new IllegalStateException("OAuth2 登录成功后未能读取到用户主键");
        }

        return jwtTokenService.issueToken(user);
    }

    public List<AuthProvider> listProviders(HttpServletRequest request) {
        if (clientRegistrationRepository == null) {
            return List.of();
        }

        if (!(clientRegistrationRepository instanceof Iterable<?> iterable)) {
            return List.of();
        }

        String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .replaceQuery(null)
                .build()
                .toUriString();

        return StreamSupport.stream(iterable.spliterator(), false)
                .filter(ClientRegistration.class::isInstance)
                .map(ClientRegistration.class::cast)
                .map(registration -> new AuthProvider(
                        registration.getRegistrationId(),
                        registration.getClientName(),
                        baseUrl + "/oauth2/authorization/" + registration.getRegistrationId()
                ))
                .toList();
    }

    public AuthUser getCurrentUser(Jwt jwt) {
        Number userIdValue = jwt.getClaim("uid");
        Long userId = userIdValue == null ? null : userIdValue.longValue();
        if (userId != null) {
            UserAccountEntity entity = userAccountDbService.getById(userId);
            if (entity != null) {
                return toAuthUser(entity);
            }
        }

        return new AuthUser(
                userId,
                jwt.getClaimAsString("name"),
                jwt.getClaimAsString("username"),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("avatarUrl"),
                jwt.getClaimAsString("provider"),
                jwt.getClaimAsString("role")
        );
    }

    private AuthUser toAuthUser(UserAccountEntity entity) {
        return new AuthUser(
                entity.getId(),
                entity.getDisplayName(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getAvatarUrl(),
                entity.getProvider(),
                entity.getRole()
        );
    }

    private String extractProviderUserId(OAuth2User oauth2User) {
        String sub = firstNonBlankValue(oauth2User.getAttribute("sub"));
        if (sub != null) {
            return sub;
        }

        String id = firstNonBlankValue(oauth2User.getAttribute("id"));
        if (id != null) {
            return id;
        }

        return oauth2User.getName();
    }

    private String extractUsername(OAuth2User oauth2User, String providerUserId) {
        return Objects.requireNonNullElse(
                firstNonBlankValues(
                        oauth2User.getAttribute("preferred_username"),
                        oauth2User.getAttribute("login"),
                        oauth2User.getAttribute("email"),
                        oauth2User.getAttribute("name")
                ),
                "user_" + providerUserId
        );
    }

    private String extractDisplayName(OAuth2User oauth2User, String username) {
        return Objects.requireNonNullElse(
                firstNonBlankValues(
                        oauth2User.getAttribute("name"),
                        oauth2User.getAttribute("login"),
                        oauth2User.getAttribute("preferred_username")
                ),
                username
        );
    }

    private String firstNonBlankValue(Object value) {
        if (value == null) {
            return null;
        }

        String text = String.valueOf(value).trim();
        if (text.isEmpty() || "null".equalsIgnoreCase(text)) {
            return null;
        }

        return text;
    }

    private String firstNonBlankValues(Object... values) {
        if (values == null || values.length == 0) {
            return null;
        }

        for (Object value : values) {
            String text = firstNonBlankValue(value);
            if (text != null) {
                return text;
            }
        }
        return null;
    }
}
