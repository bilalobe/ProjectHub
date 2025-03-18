package com.projecthub.base.auth.domain.entity;

import com.projecthub.base.shared.domain.entity.BaseEntity;
import com.projecthub.base.shared.domain.enums.security.SecurityAuditAction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
public class SecurityAuditLog extends BaseEntity {

    @Column
    private UUID userId;

    @Column
    private String username;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SecurityAuditAction action;

    @Column(length = 1000)
    private String details;

    @Column
    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column
    private String sourceSystem;

    public SecurityAuditLog() {
    }

    @PrePersist
    protected void onCreate() {
        if (null == timestamp) {
            this.timestamp = LocalDateTime.now();
        }
    }

    @Override
    public LocalDateTime getCreatedDate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCreatedDate'");
    }

    @Override
    public LocalDateTime getLastModifiedDate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLastModifiedDate'");
    }
}
