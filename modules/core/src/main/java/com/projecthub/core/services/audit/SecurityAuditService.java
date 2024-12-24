package com.projecthub.core.services.audit;

import com.projecthub.core.entities.SecurityAuditLog;
import com.projecthub.core.enums.SecurityAuditAction;
import com.projecthub.core.repositories.jpa.SecurityAuditLogRepository;
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

    public SecurityAuditService(SecurityAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAccountAction(UUID userId, SecurityAuditAction action) {
        logAccountAction(userId, action, null, null);
    }

    public void logAccountAction(UUID userId, SecurityAuditAction action, String details) {
        logAccountAction(userId, action, details, null);
    }

    public void logAccountAction(UUID userId, SecurityAuditAction action, String details, String ipAddress) {
        SecurityAuditLog log = createAuditLog(userId, null, action, details, ipAddress);
        auditLogRepository.save(log);
        logger.info("Security audit logged - User: {}, Action: {}, Details: {}",
                userId, action, details != null ? details : "none");
    }

    public void logAuthenticationAttempt(String username, boolean success, String ipAddress) {
        SecurityAuditLog log = createAuditLog(
                null,
                username,
                success ? SecurityAuditAction.LOGIN_SUCCESS : SecurityAuditAction.LOGIN_FAILURE,
                null,
                ipAddress
        );
        auditLogRepository.save(log);
        logger.info("Authentication attempt - User: {}, Success: {}", username, success);
    }

    public void logPasswordValidationFailure(UUID userId, String reason) {
        SecurityAuditLog log = createAuditLog(
            userId,
            null,
            SecurityAuditAction.PASSWORD_VALIDATION_FAILURE,
            reason,
            DEFAULT_IP_ADDRESS
        );
        auditLogRepository.save(log);
        logger.warn("Password validation failed - User: {}, Reason: {}", userId, reason);
    }

    public void logPasswordChange(UUID userId, boolean success, String ipAddress) {
        SecurityAuditLog log = createAuditLog(
            userId,
            null,
            success ? SecurityAuditAction.PASSWORD_CHANGED : SecurityAuditAction.PASSWORD_CHANGE_FAILED,
            null,
            ipAddress
        );
        auditLogRepository.save(log);
        logger.info("Password change attempt - User: {}, Success: {}", userId, success);
    }

    private SecurityAuditLog createAuditLog(
            UUID userId,
            String username,
            SecurityAuditAction action,
            String details,
            String ipAddress) {
        SecurityAuditLog log = new SecurityAuditLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setAction(action);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        log.setIpAddress(ipAddress != null ? ipAddress : DEFAULT_IP_ADDRESS);
        log.setSourceSystem(SOURCE_SYSTEM);
        return log;
    }
}