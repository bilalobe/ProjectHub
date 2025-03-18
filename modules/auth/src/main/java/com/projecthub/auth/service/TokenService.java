package com.projecthub.auth.service;

import org.springframework.security.core.Authentication;

public interface TokenService {
    String generateAccessToken(Authentication authentication);
    String generateRefreshToken(Authentication authentication);
    boolean validateToken(String token);
    Authentication getAuthentication(String token);
    String getUsernameFromToken(String token);
    boolean isTokenExpired(String token);
}