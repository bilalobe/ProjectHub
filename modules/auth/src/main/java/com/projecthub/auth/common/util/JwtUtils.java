package com.projecthub.auth.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for JWT operations like token generation, validation, and parsing.
 * Moved from foundation module to auth module to consolidate security operations.
 */
@Component
public class JwtUtils {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration:86400000}") // Default: 24 hours
    private long jwtExpiration;

    @Value("${security.jwt.refresh-expiration:604800000}") // Default: 7 days
    private long refreshExpiration;

    /**
     * Extracts the username from a JWT token.
     *
     * @param token The JWT token
     * @return The username (subject)
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token The JWT token
     * @return The expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from a JWT token.
     *
     * @param token The JWT token
     * @param claimsResolver The function to extract the desired claim
     * @return The extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token The JWT token
     * @return All claims in the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if a token is expired.
     *
     * @param token The JWT token
     * @return true if expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generates a JWT token for a user.
     *
     * @param userId The user's ID
     * @param extraClaims Additional claims to include in the token
     * @param isRefreshToken Whether this is a refresh token
     * @return The generated JWT token
     */
    public String generateToken(String userId, Map<String, Object> extraClaims, boolean isRefreshToken) {
        long expiration = isRefreshToken ? refreshExpiration : jwtExpiration;
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generates a standard JWT token for a user.
     *
     * @param userId The user's ID
     * @return The generated JWT token
     */
    public String generateToken(String userId) {
        return generateToken(userId, new HashMap<>(), false);
    }

    /**
     * Generates a refresh token for a user.
     *
     * @param userId The user's ID
     * @return The generated refresh token
     */
    public String generateRefreshToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "refresh");
        return generateToken(userId, claims, true);
    }

    /**
     * Validates a JWT token.
     *
     * @param token The JWT token
     * @param userId The user ID to validate against
     * @return true if valid, false otherwise
     */
    public Boolean validateToken(String token, String userId) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(userId) && !isTokenExpired(token));
    }

    /**
     * Creates a signing key from the secret.
     *
     * @return The signing key
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}