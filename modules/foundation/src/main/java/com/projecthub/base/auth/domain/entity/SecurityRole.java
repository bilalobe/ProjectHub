package com.projecthub.base.auth.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "security_roles")
@NoArgsConstructor
@Getter
public class SecurityRole implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String authority;

    public SecurityRole(String authority) {
        this.authority = authority;
    }
}
