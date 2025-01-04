package com.projecthub.base.auth.domain.entity;

import com.projecthub.base.user.domain.entity.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;

@Entity
@Table(name = "security_users")
@NoArgsConstructor
@Getter
public class SecurityUser implements UserDetails {

    private static final boolean IS_ENABLED = true;
    private static final boolean IS_ACCOUNT_NON_EXPIRED = true;
    private static final boolean IS_ACCOUNT_NON_LOCKED = true;
    private static final boolean IS_CREDENTIALS_NON_EXPIRED = true;

    @ManyToMany(fetch = FetchType.EAGER)
    private final Collection<SecurityRole> authorities = new HashSet<>();

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private AppUser appUser;

    private String password;

    public SecurityUser(AppUser appUser) {
        this.appUser = appUser;
    }

    @Override
    public String getUsername() {
        return appUser.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return IS_ACCOUNT_NON_EXPIRED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return IS_ACCOUNT_NON_LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return IS_CREDENTIALS_NON_EXPIRED;
    }

    @Override
    public boolean isEnabled() {
        return IS_ENABLED;
    }

    public void addAuthority(SecurityRole authority) {
        authorities.add(authority);
    }

    public void removeAuthority(SecurityRole authority) {
        authorities.remove(authority);
    }

    public AppUser getAppUser() {
        return appUser;
    }
}
