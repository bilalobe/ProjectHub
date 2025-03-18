package com.projecthub.base.auth.service.fortress;

import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.model.Session;
import org.apache.directory.fortress.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Maps Fortress sessions and users to Spring Security user details.
 */
@Component
public class FortressUserDetailsMapper {

    private static final Logger log = LoggerFactory.getLogger(FortressUserDetailsMapper.class);
    private final ReviewMgr reviewManager;

    public FortressUserDetailsMapper(ReviewMgr reviewManager) {
        this.reviewManager = reviewManager;
    }

    /**
     * Creates UserDetails from a Fortress session.
     *
     * @param username the username
     * @param session the Fortress session
     * @return FortressUserDetails
     */
    public FortressUserDetails createUserDetails(String username, Session session) {
        try {
            // Read full user details from Fortress
            User user = reviewManager.readUser(new User(username));

            // Map roles to authorities
            List<String> roles = session.getRoles();
            Collection<GrantedAuthority> authorities = mapToAuthorities(roles);

            // Create user details
            String userId = user.getUserId() != null ? user.getUserId() : UUID.randomUUID().toString();
            return new FortressUserDetails(
                userId,
                username,
                authorities,
                !user.isLocked(), // If not locked, then account is non-expired
                !user.isLocked(), // If not locked, then account is non-locked
                true,             // Assume credentials non-expired
                user.isEnabled(),
                user.getEmail()
            );

        } catch (RuntimeException e) {
            log.error("Error mapping Fortress user to UserDetails: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to map user details", e);
        }
    }

    /**
     * Map role names to Spring Security authorities.
     *
     * @param roles list of role names
     * @return collection of granted authorities
     */
    private static Collection<GrantedAuthority> mapToAuthorities(Collection<String> roles) {
        if (roles == null) {
            return List.of();
        }

        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase(Locale.ROOT)))
            .collect(Collectors.toList());
    }
}
