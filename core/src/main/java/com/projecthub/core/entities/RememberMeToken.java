package com.projecthub.core.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a remember-me authentication token.
 * <p>
 * This entity stores persistent tokens for the "remember me" functionality in Spring Security.
 * Each token is associated with a user and has an expiry date. The token consists of two
 * parts: a series identifier and a token value.
 * </p>
 * <p>
 * Business rules:
 * <ul>
 *   <li>Each token must have a unique series identifier</li>
 *   <li>Tokens must have an expiry date</li>
 *   <li>Tokens must be associated with a user</li>
 *   <li>Token values should be regenerated on each successful authentication</li>
 * </ul>
 * </p>
 *
 * @see AppUser
 */
@Entity
@Data
@Table
public class RememberMeToken extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String series;

    private String tokenValue;

    @Column(nullable = false)
    private LocalDateTime lastUsed;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private AppUser user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RememberMeToken that = (RememberMeToken) o;
        return Objects.equals(series, that.series);
    }

    @Override
    public int hashCode() {
        return Objects.hash(series);
    }
}