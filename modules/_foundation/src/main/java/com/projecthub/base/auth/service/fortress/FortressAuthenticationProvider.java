package com.projecthub.base.auth.service.fortress;

import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.model.Session;
import org.apache.directory.fortress.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Authentication provider that uses Apache Fortress for authentication and authorization.
 * This provider integrates with Spring Security's authentication framework.
 */
@Component
public class FortressAuthenticationProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(FortressAuthenticationProvider.class);
    private final AccessMgr accessManager;
    private final FortressUserDetailsMapper userDetailsMapper;

    public FortressAuthenticationProvider(AccessMgr accessManager, FortressUserDetailsMapper userDetailsMapper) {
        this.accessManager = accessManager;
        this.userDetailsMapper = userDetailsMapper;
    }

    /**
     * @param authentication the authentication request object.
     * @return
     * @throws BadCredentialsException
     */
    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        log.debug("Authenticating user: {}", username);

        try {
            // Authenticate the user with Fortress
            Session session = accessManager.createSession(
                new User(username, password), false);

            // Extract roles and permissions from the Fortress session
            List<String> roles = session.getRoles();
            Collection<GrantedAuthority> authorities = mapRolesToAuthorities(roles);

            // Create Spring Security user details from Fortress session
            FortressUserDetails userDetails = userDetailsMapper.createUserDetails(username, session);

            // Create authenticated token with authorities
            UsernamePasswordAuthenticationToken authenticatedToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            authenticatedToken.setDetails(session);

            log.info("Successfully authenticated user: {}", username);
            return authenticatedToken;

        } catch (org.apache.directory.fortress.core.SecurityException e) {
            log.warn("Authentication failed for user {}: {}", username, e.getMessage());
            throw new BadCredentialsException("Authentication failed", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    /**
     * Maps Fortress roles to Spring Security GrantedAuthority objects.
     *
     * @param roles list of roles from Fortress session
     * @return collection of Spring Security authorities
     */
    private static Collection<GrantedAuthority> mapRolesToAuthorities(Collection<String> roles) {
        if (roles == null) {
            return new ArrayList<>();
        }

        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase(Locale.ROOT)))
            .collect(Collectors.toList());
    }
}
