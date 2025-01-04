package com.projecthub.base.shared.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for handling JSON Web Tokens (JWT).
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    /**
     * Secret key for signing JWTs.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT expiration time in seconds.
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Generates a signing key using the secret.
     *
     * @return the signing key.
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token for a given username.
     *
     * @param username the username for which to generate the token.
     * @return the generated JWT token.
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, username);
    }

    /**
     * Generates a JWT token with claims and subject.
     *
     * @param claims  the claims to include in the token.
     * @param subject the subject (username) of the token.
     * @return the generated JWT token.
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        Key key = getSigningKey();
        return Jwts.builder()
            .subject(subject)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration * 1000))
            .claims().add(claims).and()
            .signWith(key)
            .compact();
    }

    /**
     * Retrieves the username from a JWT token.
     *
     * @param token the JWT token.
     * @return the username extracted from the token.
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Retrieves the expiration date from a JWT token.
     *
     * @param token the JWT token.
     * @return the expiration date of the token.
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Retrieves a specific claim from a JWT token.
     *
     * @param <T>            the type of the claim.
     * @param token          the JWT token.
     * @param claimsResolver a function to extract the desired claim.
     * @return the extracted claim.
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Retrieves all claims from a JWT token.
     *
     * @param token the JWT token.
     * @return the claims extracted from the token.
     */
    private Claims getAllClaimsFromToken(String token) {
        Key key = getSigningKey();
        return Jwts.parser()
            .verifyWith((SecretKey) key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Enhanced token validation with multiple checks.
     */
    public boolean validateToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return false;
            }

            SecretKey key = (SecretKey) getSigningKey();
            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            // Check if token is expired
            if (claims.getExpiration().before(new Date())) {
                logger.debug("Token is expired");
                return false;
            }

            // Check if token was issued in the future
            if (claims.getIssuedAt().after(new Date())) {
                logger.debug("Token was issued in the future");
                return false;
            }

            return true;
        } catch (Exception e) {
            logger.error("Token validation failed", e);
            return false;
        }
    }

    /**
     * Validate token for a specific username.
     */
    public boolean validateToken(String token, String username) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            return (tokenUsername.equals(username) && validateToken(token));
        } catch (Exception e) {
            logger.error("Token validation failed for user: {}", username, e);
            return false;
        }
    }
}
