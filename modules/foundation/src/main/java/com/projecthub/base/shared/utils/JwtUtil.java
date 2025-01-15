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
        final byte[] keyBytes = this.secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token for a given username.
     *
     * @param username the username for which to generate the token.
     * @return the generated JWT token.
     */
    public String generateToken(final String username) {
        final Map<String, Object> claims = new HashMap<>();
        return this.doGenerateToken(claims, username);
    }

    /**
     * Generates a JWT token with claims and subject.
     *
     * @param claims  the claims to include in the token.
     * @param subject the subject (username) of the token.
     * @return the generated JWT token.
     */
    private String doGenerateToken(final Map<String, Object> claims, final String subject) {
        final Key key = this.getSigningKey();
        return Jwts.builder()
            .subject(subject)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + this.expiration * 1000))
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
    public String getUsernameFromToken(final String token) {
        return this.getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Retrieves the expiration date from a JWT token.
     *
     * @param token the JWT token.
     * @return the expiration date of the token.
     */
    public Date getExpirationDateFromToken(final String token) {
        return this.getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Retrieves a specific claim from a JWT token.
     *
     * @param <T>            the type of the claim.
     * @param token          the JWT token.
     * @param claimsResolver a function to extract the desired claim.
     * @return the extracted claim.
     */
    public <T> T getClaimFromToken(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = this.getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Retrieves all claims from a JWT token.
     *
     * @param token the JWT token.
     * @return the claims extracted from the token.
     */
    private Claims getAllClaimsFromToken(final String token) {
        final Key key = this.getSigningKey();
        return Jwts.parser()
            .verifyWith((SecretKey) key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Enhanced token validation with multiple checks.
     */
    public boolean validateToken(final String token) {
        try {
            if (null == token || token.isEmpty()) {
                return false;
            }

            final SecretKey key = (SecretKey) this.getSigningKey();
            final Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            // Check if token is expired
            if (claims.getExpiration().before(new Date())) {
                JwtUtil.logger.debug("Token is expired");
                return false;
            }

            // Check if token was issued in the future
            if (claims.getIssuedAt().after(new Date())) {
                JwtUtil.logger.debug("Token was issued in the future");
                return false;
            }

            return true;
        } catch (final Exception e) {
            JwtUtil.logger.error("Token validation failed", e);
            return false;
        }
    }

    /**
     * Validate token for a specific username.
     */
    public boolean validateToken(final String token, final String username) {
        try {
            final String tokenUsername = this.getUsernameFromToken(token);
            return (tokenUsername.equals(username) && this.validateToken(token));
        } catch (final Exception e) {
            JwtUtil.logger.error("Token validation failed for user: {}", username, e);
            return false;
        }
    }
}
