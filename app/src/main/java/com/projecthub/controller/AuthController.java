package com.projecthub.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projecthub.dto.AppUserDTO;
import com.projecthub.dto.LoginRequestDTO;
import com.projecthub.dto.RegisterRequestDTO;
import com.projecthub.service.AuthService;
import com.projecthub.util.JwtUtil;

import jakarta.validation.Valid;

/**
 * AuthController is a REST controller that handles authentication-related requests.
 * It provides endpoints for user registration and login.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public AppUserDTO registerUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtil.generateToken(loginRequest.getUsername());
    }
}