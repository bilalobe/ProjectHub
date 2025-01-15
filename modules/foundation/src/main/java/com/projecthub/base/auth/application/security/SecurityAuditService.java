package com.projecthub.base.auth.application.security;

import com.projecthub.base.auth.domain.entity.SecurityAuditLog;
import com.projecthub.base.repository.jpa.SecurityAuditLogRepository;
import com.projecthub.base.shared.domain.enums.security.SecurityAuditAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class SecurityAuditService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAuditService.class);
    private static final String DEFAULT_IP_ADDRESS = "0.0.0.0";
    private static final String SOURCE_SYSTEM = "WEB";

    private final SecurityAuditLogRepository auditLogRepository;

    public SecurityAuditService(final SecurityAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAccountAction(final UUID userId, final SecurityAuditAction action) {
        this.logAccountAction(userId, action, null, null);
    }

    public void logAccountAction(final UUID userId, final SecurityAuditAction action, final String details) {
        this.logAccountAction(userId, action, details, null);
    }

    public void logAccountAction(final UUID userId, final SecurityAuditAction action, final String details, final String ipAddress) {
        final SecurityAuditLog log = this.createAuditLog(userId, null, action, details, ipAddress);
        this.auditLogRepository.save(log);
        SecurityAuditService.logger.info("Security audit logged - User: {}, Action: {}, Details: {}",
            userId, action, null != details ? details : "none");
    }

    public void logAuthenticationAttempt(final String username, final boolean success, final String ipAddress) {
        final SecurityAuditLog log = this.createAuditLog(
            null,
            username,
            success ? SecurityAuditAction.LOGIN_SUCCESS : SecurityAuditAction.LOGIN_FAILURE,
            null,
            ipAddress
        );
        this.auditLogRepository.save(log);
        SecurityAuditService.logger.info("Authentication attempt - User: {}, Success: {}", username, success);
    }

    public void logPasswordValidationFailure(final UUID userId, final String reason) {
        final SecurityAuditLog log = this.createAuditLog(
            userId,
            null,
            SecurityAuditAction.PASSWORD_VALIDATION_FAILURE,
            reason,
            SecurityAuditService.DEFAULT_IP_ADDRESS
        );
        this.auditLogRepository.save(log);
        SecurityAuditService.logger.warn("Password validation failed - User: {}, Reason: {}", userId, reason);
    }

    public void logPasswordChange(final UUID userId, final boolean success, final String ipAddress) {
        final SecurityAuditLog log = this.createAuditLog(
            userId,
            null,
            success ? SecurityAuditAction.PASSWORD_CHANGED : SecurityAuditAction.PASSWORD_CHANGE_FAILED,
            null,
            ipAddress
        );
        this.auditLogRepository.save(log);
        SecurityAuditService.logger.info("Password change attempt - User: {}, Success: {}", userId, success);
    }

    private com.projecthub.base.auth.domain.entity.SecurityAuditLog createAuditLog(
        final UUID userId,
        final String username,
        final com.projecthub.base.auth.domain.entity.SecurityAuditLog action,
        final String details,
        final String ipAddress) {
        final SecurityAuditLog log = new SecurityAuditLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setAction(action);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        log.setIpAddress(null != ipAddress ? ipAddress : SecurityAuditService.DEFAULT_IP_ADDRESS);
        log.setSourceSystem(SecurityAuditService.SOURCE_SYSTEM);
        return log;
    }

    public int countRecentFailedAttempts(final String username, final String ipAddress, final LocalDateTime localDateTime) {
        return 0;
    }
}
