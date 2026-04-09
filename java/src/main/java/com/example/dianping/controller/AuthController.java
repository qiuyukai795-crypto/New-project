package com.example.dianping.controller;

import com.example.dianping.model.AuthProvider;
import com.example.dianping.model.AuthToken;
import com.example.dianping.model.AuthUser;
import com.example.dianping.model.LocalLoginRequest;
import com.example.dianping.model.LocalRegisterRequest;
import com.example.dianping.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/login")
    public AuthToken login(@Valid @RequestBody LocalLoginRequest request) {
        return authService.loginWithLocalPassword(request.username(), request.password());
    }

    @PostMapping("/register")
    public AuthToken register(@Valid @RequestBody LocalRegisterRequest request) {
        return authService.registerLocalAccount(request.username(), request.password(), request.displayName(), request.email());
    }

    @GetMapping("/me")
    public AuthUser currentUser(@AuthenticationPrincipal Jwt jwt) {
        return authService.getCurrentUser(jwt);
    }
}
