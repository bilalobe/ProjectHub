package com.projecthub.base.auth.domain.entity;

import com.projecthub.base.shared.domain.entity.BaseEntity;
import com.projecthub.base.user.domain.entity.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "user")
public class PasswordResetToken extends BaseEntity {

    @NotBlank(message = "Token is mandatory")
    @Column(nullable = false, unique = true)
    private String token;

    @NotNull(message = "User is mandatory")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private AppUser user;

    @NotNull(message = "Expiry date is mandatory")
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public PasswordResetToken(String token, AppUser user, LocalDateTime expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

    @PrePersist
    protected void onCreate() {
        if (this.expiryDate == null) {
            this.expiryDate = LocalDateTime.now().plusHours(24); // Set expiry date to 24 hours from creation
        }
    }
}
