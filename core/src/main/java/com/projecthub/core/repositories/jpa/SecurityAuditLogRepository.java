package com.projecthub.core.repositories.jpa;

import com.projecthub.core.models.SecurityAuditAction;
import com.projecthub.core.models.SecurityAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SecurityAuditLogRepository extends JpaRepository<SecurityAuditLog, UUID> {

    List<SecurityAuditLog> findByUserIdOrderByTimestampDesc(UUID userId);

    Page<SecurityAuditLog> findByActionOrderByTimestampDesc(
            SecurityAuditAction action,
            Pageable pageable
    );

    List<SecurityAuditLog> findByUsernameAndTimestampBetween(
            String username,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("SELECT s FROM SecurityAuditLog s WHERE s.timestamp >= :since " +
            "AND s.action IN ('LOGIN_FAILURE', 'ACCOUNT_LOCKED')")
    List<SecurityAuditLog> findSecurityIncidentsSince(LocalDateTime since);

    @Query("SELECT COUNT(s) FROM SecurityAuditLog s WHERE s.userId = :userId " +
            "AND s.action = 'LOGIN_FAILURE' AND s.timestamp >= :since")
    long countFailedLoginAttempts(UUID userId, LocalDateTime since);
}
