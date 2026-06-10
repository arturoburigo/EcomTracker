package com.api.ecomtracker.service;

import com.api.ecomtracker.domain.User;
import com.api.ecomtracker.dto.auth.LoginRequest;
import com.api.ecomtracker.dto.auth.TokenResponse;
import com.api.ecomtracker.security.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    public AuthenticationService(
            AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public TokenResponse login(LoginRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(), request.getPassword()));
        User user = (User) authentication.getPrincipal();
        return new TokenResponse(tokenService.generateToken(user));
    }
}
