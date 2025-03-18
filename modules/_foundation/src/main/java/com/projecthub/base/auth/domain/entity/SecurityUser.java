package com.projecthub.base.auth.domain.entity;

import com.projecthub.base.shared.domain.entity.BaseEntity;
import com.projecthub.base.user.domain.entity.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NonNls;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Entity representing the security context for a user in the ProjectHub system.
 * <p>
 * This entity implements Spring Security's {@link UserDetails} interface and acts as the
 * main integration point with Apache Fortress RBAC. It separates security concerns from
 * the user's profile information which is stored in {@link AppUser}.
 * </p>
 */
@Entity
@Table(name = "security_users")
@NoArgsConstructor
@Getter
public class SecurityUser extends BaseEntity implements UserDetails {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser appUser;

    @Column(nullable = false)
    @Setter
    private String password;

    @Setter
    private String verificationToken;

    @Setter
    private LocalDateTime verificationTokenExpiry;

    @Setter
    private boolean verified;

    @Setter
    private boolean enabled = false;

    @Setter
    private boolean accountNonLocked = true;

    @Setter
    private int failedAttempts;

    @Setter
    private LocalDateTime lastLoginAttempt;

    @Setter
    private LocalDateTime lastSuccessfulLogin;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "security_user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private final Set<SecurityRole> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "security_user_permissions",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private final Set<Permission> permissions = new HashSet<>();

    /**
     * Creates a new SecurityUser linked to the given AppUser.
     *
     * @param appUser The application user to link to
     * @param password The encoded password for authentication
     */
    public SecurityUser(final AppUser appUser, final String password) {
        this.appUser = appUser;
        this.password = password;
    }

    @Override
    public String getUsername() {
        return this.appUser.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new HashSet<>();

        // Add role-based authorities
        authorities.addAll(this.roles.stream()
            .map(role -> (GrantedAuthority) () -> "ROLE_" + role.getName())
            .collect(Collectors.toSet()));

        // Add permission-based authorities
        authorities.addAll(this.permissions.stream()
            .map(permission -> (GrantedAuthority) permission::getName)
            .collect(Collectors.toSet()));

        return authorities;
    }

    /**
     * Gets the roles assigned to this security user.
     *
     * @return The set of roles
     */
    public Set<SecurityRole> getRoles() {
        return this.roles;
    }

    /**
     * Gets the direct permissions assigned to this security user.
     *
     * @return The set of permissions
     */
    public Set<Permission> getPermissions() {
        return this.permissions;
    }

    /**
     * Adds a role to this security user.
     *
     * @param role The role to add
     */
    public void addRole(final SecurityRole role) {
        this.roles.add(role);
    }

    /**
     * Removes a role from this security user.
     *
     * @param role The role to remove
     */
    public void removeRole(final SecurityRole role) {
        this.roles.remove(role);
    }

    /**
     * Adds a permission to this security user.
     *
     * @param permission The permission to add
     */
    public void addPermission(final Permission permission) {
        this.permissions.add(permission);
    }

    /**
     * Removes a permission from this security user.
     *
     * @param permission The permission to remove
     */
    public void removePermission(final Permission permission) {
        this.permissions.remove(permission);
    }

    /**
     * Checks if the user has the specified permission.
     *
     * @param permissionName The permission name to check
     * @return true if the user has the permission, false otherwise
     */
    public boolean hasPermission(@NonNls String permissionName) {
        return this.permissions.stream()
            .anyMatch(p -> p.getName().equals(permissionName));
    }

    /**
     * Checks if the user has the specified role.
     *
     * @param roleName The role name to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String roleName) {
        return this.roles.stream()
            .anyMatch(r -> r.getName().equals(roleName));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Account expiration is not used in this implementation
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Credentials expiration is not used in this implementation
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
