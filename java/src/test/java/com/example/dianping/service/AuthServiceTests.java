package com.example.dianping.service;

import com.example.dianping.entity.UserAccountEntity;
import com.example.dianping.model.AuthToken;
import com.example.dianping.security.AppSecurityProperties;
import com.example.dianping.security.JwtTokenService;
import com.example.dianping.service.db.UserAccountDbService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private UserAccountDbService userAccountDbService;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ObjectProvider<ClientRegistrationRepository> clientRegistrationRepositoryProvider;

    @Spy
    private AppSecurityProperties securityProperties = new AppSecurityProperties();

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        securityProperties.getProviders().getGithub().setEnabled(true);
        securityProperties.getProviders().getLocal().setEnabled(true);
    }

    @Test
    void handleOAuth2LoginShouldAcceptMissingProviderIdentifiersWithoutThrowing() {
        OAuth2User oauth2User = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("login", "octocat", "name", "The Octocat"),
                "login"
        );
        OAuth2AuthenticationToken authenticationToken =
                new OAuth2AuthenticationToken(oauth2User, oauth2User.getAuthorities(), "github");

        UserAccountEntity persistedUser = new UserAccountEntity();
        persistedUser.setId(1L);
        persistedUser.setProvider("github");
        persistedUser.setProviderUserId("octocat");
        persistedUser.setUsername("octocat");
        persistedUser.setDisplayName("The Octocat");
        persistedUser.setRole("ROLE_USER");

        when(userAccountDbService.getOne(any())).thenReturn(null, persistedUser);
        doAnswer(invocation -> true).when(userAccountDbService).saveOrUpdate(any(UserAccountEntity.class));
        AuthToken expectedToken = new AuthToken("token-value", Instant.parse("2026-04-09T08:00:00Z"));
        when(jwtTokenService.issueToken(any(UserAccountEntity.class))).thenReturn(expectedToken);

        AuthToken authToken = authService.handleOAuth2Login(authenticationToken);

        assertThat(authToken).isEqualTo(expectedToken);
        assertThat(authToken.accessToken()).isEqualTo("token-value");
    }

    @Test
    void handleOAuth2LoginShouldAcceptNumericGithubId() {
        OAuth2User oauth2User = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("id", 123456, "login", "octocat", "name", "The Octocat"),
                "login"
        );
        OAuth2AuthenticationToken authenticationToken =
                new OAuth2AuthenticationToken(oauth2User, oauth2User.getAuthorities(), "github");

        UserAccountEntity persistedUser = new UserAccountEntity();
        persistedUser.setId(2L);
        persistedUser.setProvider("github");
        persistedUser.setProviderUserId("123456");
        persistedUser.setUsername("octocat");
        persistedUser.setDisplayName("The Octocat");
        persistedUser.setRole("ROLE_USER");

        when(userAccountDbService.getOne(any())).thenReturn(null, persistedUser);
        doAnswer(invocation -> true).when(userAccountDbService).saveOrUpdate(any(UserAccountEntity.class));
        AuthToken expectedToken = new AuthToken("token-value-2", Instant.parse("2026-04-09T08:30:00Z"));
        when(jwtTokenService.issueToken(any(UserAccountEntity.class))).thenReturn(expectedToken);

        AuthToken authToken = authService.handleOAuth2Login(authenticationToken);

        assertThat(authToken).isEqualTo(expectedToken);
        assertThat(authToken.accessToken()).isEqualTo("token-value-2");
    }

    @Test
    void loginWithLocalPasswordShouldIssueJwtWhenPasswordMatches() {
        UserAccountEntity localUser = new UserAccountEntity();
        localUser.setId(3L);
        localUser.setProvider("local");
        localUser.setProviderUserId("demo");
        localUser.setUsername("demo");
        localUser.setDisplayName("Demo User");
        localUser.setPasswordHash("encoded-password");
        localUser.setRole("ROLE_USER");

        when(userAccountDbService.getOne(any())).thenReturn(localUser);
        when(passwordEncoder.matches("123456", "encoded-password")).thenReturn(true);
        AuthToken expectedToken = new AuthToken("local-token", Instant.parse("2026-04-09T09:00:00Z"));
        when(jwtTokenService.issueToken(localUser)).thenReturn(expectedToken);

        AuthToken authToken = authService.loginWithLocalPassword("demo", "123456");

        assertThat(authToken).isEqualTo(expectedToken);
    }
}
