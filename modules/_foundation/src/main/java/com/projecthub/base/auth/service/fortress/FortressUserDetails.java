package com.projecthub.base.auth.service.fortress;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

/**
 * Implementation of Spring Security's UserDetails that wraps a Fortress user.
 * This class provides the bridge between Apache Fortress and Spring Security.
 */
public class FortressUserDetails implements UserDetails {

    private final String username;
    private final String userId;
    private final Collection<GrantedAuthority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    private final String email;
    
    public FortressUserDetails(
            String userId,
            String username,
            Collection<GrantedAuthority> authorities,
            boolean accountNonExpired,
            boolean accountNonLocked,
            boolean credentialsNonExpired,
            boolean enabled,
            String email) {
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
        this.email = email;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        // Password is not stored in UserDetails for security reasons
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }
    
    public UUID getUserId() {
        return UUID.fromString(userId);
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    public String getEmail() {
        return email;
    }
}