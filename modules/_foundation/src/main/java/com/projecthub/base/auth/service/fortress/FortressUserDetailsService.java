package com.projecthub.base.auth.service.fortress;

import org.apache.directory.fortress.core.ReviewMgr;
import org.apache.directory.fortress.core.model.User;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implements Spring Security's UserDetailsService using Apache Fortress.
 * This loads user details for initial authentication without validating passwords.
 */
@Service
public class FortressUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(FortressUserDetailsService.class);
    private final ReviewMgr reviewManager;

    public FortressUserDetailsService(ReviewMgr reviewManager) {
        this.reviewManager = reviewManager;
    }

    /**
     * @param username the username identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(@NonNls @NonNls String username) {
        try {
            log.debug("Loading user by username: {}", username);

            // Read user from Fortress
            User user = reviewManager.readUser(new User(username));
            if (user == null) {
                throw new UsernameNotFoundException("User not found: " + username);
            }

            // Get assigned roles for the user
            List<String> roleNames = reviewManager.assignedRoles(user);

            // Convert to Spring Security authorities
            var authorities = roleNames.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase(Locale.ROOT)))
                .collect(Collectors.toList());

            // Create user details
            String userId = user.getUserId() != null ? user.getUserId() : UUID.randomUUID().toString();
            return new FortressUserDetails(
                userId,
                username,
                authorities,
                !user.isLocked(),
                !user.isLocked(),
                true,
                user.isEnabled(),
                user.getEmail()
            );

        } catch (org.apache.directory.fortress.core.SecurityException e) {
            log.error("Failed to load user by username: {}", username, e);
            throw new UsernameNotFoundException("Failed to load user: " + username, e);
        }
    }
}
