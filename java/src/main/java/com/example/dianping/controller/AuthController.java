package com.example.dianping.controller;

import com.example.dianping.model.AuthProvider;
import com.example.dianping.model.AuthUser;
import com.example.dianping.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/providers")
    public List<AuthProvider> listProviders(HttpServletRequest request) {
        return authService.listProviders(request);
    }

    @GetMapping("/me")
    public AuthUser currentUser(@AuthenticationPrincipal Jwt jwt) {
        return authService.getCurrentUser(jwt);
    }
}
