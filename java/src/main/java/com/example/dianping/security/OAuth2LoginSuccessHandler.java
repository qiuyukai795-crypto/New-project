package com.example.dianping.security;

import com.example.dianping.model.AuthToken;
import com.example.dianping.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

    private final AuthService authService;
    private final AppSecurityProperties securityProperties;

    public OAuth2LoginSuccessHandler(AuthService authService, AppSecurityProperties securityProperties) {
        this.authService = authService;
        this.securityProperties = securityProperties;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (!(authentication instanceof OAuth2AuthenticationToken oauth2Token)) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        try {
            AuthToken authToken = authService.handleOAuth2Login(oauth2Token);
            String redirectUrl = securityProperties.getFrontendBaseUrl()
                    + "/auth/callback#token="
                    + URLEncoder.encode(authToken.accessToken(), StandardCharsets.UTF_8)
                    + "&expiresAt="
                    + URLEncoder.encode(authToken.expiresAt().toString(), StandardCharsets.UTF_8);

            clearAuthenticationAttributes(request);
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } catch (Exception exception) {
            log.error("OAuth2 登录成功后的本地令牌签发失败", exception);
            String redirectUrl = securityProperties.getFrontendBaseUrl() + "/auth/callback#error=oauth_processing_failed";
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
    }
}
