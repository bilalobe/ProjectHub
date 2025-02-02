package com.projecthub.base.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.projecthub.base.auth.domain.entity.Role;
import com.projecthub.base.shared.domain.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Entity representing a user in the ProjectHub system.
 * <p>
 * This entity implements Spring Security's {@link UserDetails} interface to provide
 * authentication and authorization capabilities. Users can be assigned multiple roles
 * and can be associated with various entities in the system.
 * </p>
 * <p>
 * Business rules:
 * <ul>
 *   <li>Each user must have a unique username</li>
 *   <li>Email addresses must be unique and valid</li>
 *   <li>Passwords are stored securely and never exposed in JSON responses</li>
 *   <li>Users can have multiple roles</li>
 *   <li>Username and email are immutable after creation</li>
 * </ul>
 * </p>
 * </p>
 *
 * @see Role
 * @see UserDetails
 */
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    indexes = {
        @Index(name = "idx_user_username", columnList = "username"),
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_verification_token", columnList = "verificationToken")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uc_user_username", columnNames = "username"),
        @UniqueConstraint(name = "uc_user_email", columnNames = "email")
    }
)
@ToString(exclude = {"password", "roles"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public class AppUser extends BaseEntity implements UserDetails {

    // region Identity Fields
    @EqualsAndHashCode.Include
    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(nullable = false, unique = true)
    String username;

    @NotBlank(message = "Password is mandatory")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Setter
    @Column(nullable = false)
    String password;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    String email;

    @NotBlank(message = "First name is mandatory")
    @Column(nullable = false)
    String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Column(nullable = false)
    String lastName;
    // endregion

    // region Profile Fields
    @Setter
    @Column(length = 500)
    String statusMessage;

    @Setter
    String avatarUrl;
    // endregion

    // region Statistics Fields
    @Builder.Default
    int postCount;

    @Builder.Default
    int followerCount;

    @Builder.Default
    int followingCount;
    // endregion

    // region Security Fields
    @Builder.Default
    @Setter
    boolean enabled;

    @Builder.Default
    @Setter
    boolean accountNonLocked = true;

    @Builder.Default
    int failedAttempts;

    LocalDateTime lastLoginAttempt;
    // endregion

    // region Verification Fields
    @Setter
    String verificationToken;

    @Setter
    LocalDateTime verificationTokenExpiry;

    @Builder.Default
    @Setter
    boolean verified;
    // endregion

    @Builder.Default
    @Setter(AccessLevel.PROTECTED)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "app_user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * Builder pattern for creating AppUser instances.
     *
     * @param username  Unique username for the user.
     * @param password  Encrypted password.
     * @param firstName User's first name.
     * @param lastName  User's last name.
     * @param email     Unique and validated email address.
     */
    public AppUser(final String username, final String password, final String firstName, final String lastName, final String email) {
        this.validateFields(username, password, firstName, lastName, email);
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Validates mandatory fields during user creation.
     *
     * @param username  Username to validate.
     * @param password  Password to validate.
     * @param firstName First name to validate.
     * @param lastName  Last name to validate.
     * @param email     Email to validate.
     */
    private void validateFields(final String username, final String password, final String firstName, final String lastName, final String email) {
        if (null == username || username.isEmpty()) {
            throw new IllegalArgumentException("Username is mandatory");
        }
        if (null == password || password.isEmpty()) {
            throw new IllegalArgumentException("Password is mandatory");
        }
        if (null == firstName || firstName.isEmpty()) {
            throw new IllegalArgumentException("First name is mandatory");
        }
        if (null == lastName || lastName.isEmpty()) {
            throw new IllegalArgumentException("Last name is mandatory");
        }
        if (null == email || email.isEmpty()) {
            throw new IllegalArgumentException("Email is mandatory");
        }
    }

    /**
     * Retrieves the authorities granted to the user.
     *
     * @return a collection of granted authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
            .map(role -> (GrantedAuthority) () -> "ROLE_" + role.getName())
            .collect(Collectors.toSet());
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true if the account is non-expired, false otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return true if the account is not locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     *
     * @return true if the credentials are non-expired, false otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return true if the user is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Increments the post count by one.
     */
    void incrementPostCount() {
        postCount++;
    }

    /**
     * Increments the follower count by one.
     */
    void incrementFollowerCount() {
        followerCount++;
    }

    /**
     * Decrements the follower count by one if greater than zero.
     */
    void decrementFollowerCount() {
        if (0 < this.followerCount) {
            followerCount--;
        }
    }

    /**
     * Increments the following count by one.
     */
    void incrementFollowingCount() {
        followingCount++;
    }

    /**
     * Decrements the following count by one if greater than zero.
     */
    void decrementFollowingCount() {
        if (0 < this.followingCount) {
            followingCount--;
        }
    }

    /**
     * Updates the last login attempt time to the current time.
     */
    void updateLastLoginAttempt() {
        lastLoginAttempt = LocalDateTime.now();
    }

    /**
     * Sets the account lock status.
     *
     * @param locked true to lock the account, false to unlock.
     */
    public void setLocked(final boolean locked) {
        accountNonLocked = !locked;
    }

    /**
     * Increments the number of failed login attempts by one.
     *
     * @return the updated number of failed attempts.
     */
    public int incrementFailedAttempts() {
        ++failedAttempts;
        return failedAttempts;
    }

    /**
     * Resets the number of failed login attempts to zero.
     */
    public void resetFailedAttempts() {
        failedAttempts = 0;
    }

    /**
     * Sets the number of failed login attempts.
     *
     * @param attempts the number of failed attempts to set.
     * @throws IllegalArgumentException if attempts is negative.
     */
    public void setFailedAttempts(final int attempts) {
        if (0 > attempts) {
            throw new IllegalArgumentException("Failed attempts cannot be negative");
        }
        failedAttempts = attempts;

        if (5 <= attempts) {
            this.setLocked(true);
        }
    }
}
