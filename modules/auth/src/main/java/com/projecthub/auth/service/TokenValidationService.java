package com.projecthub.auth.service;

import java.util.Map;
import java.util.Optional;

/**
 * Service responsible for validating and extracting information from authentication tokens.
 */
public interface TokenValidationService {
    
    /**
     * Validates if a token is authentic and not expired.
     *
     * @param token The token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean isValid(String token);
    
    /**
     * Extracts the subject (usually user ID) from a token.
     *
     * @param token The token to extract from
     * @return The subject identifier or empty if invalid
     */
    Optional<String> extractSubject(String token);
    
    /**
     * Extracts all claims from a token.
     *
     * @param token The token to extract from
     * @return Map of claim names to values, or empty if invalid
     */
    Map<String, Object> extractClaims(String token);
    
    /**
     * Extracts a specific claim from a token.
     *
     * @param token The token to extract from
     * @param claimName The name of the claim to extract
     * @return The claim value or empty if not present or invalid
     */
    <T> Optional<T> extractClaim(String token, String claimName, Class<T> claimType);
}