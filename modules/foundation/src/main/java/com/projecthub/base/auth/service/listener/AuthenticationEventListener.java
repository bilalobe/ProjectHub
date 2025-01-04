package com.projecthub.base.auth.service.listener;

import com.projecthub.base.auth.application.security.SecurityAuditService;
import com.projecthub.base.auth.domain.event.AuthenticationEvent;
import com.projecthub.base.shared.domain.enums.security.SecurityAuditAction;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEventListener {
    private final SecurityAuditService auditService;

    public AuthenticationEventListener(SecurityAuditService auditService) {
        this.auditService = auditService;
    }

    @EventListener
    public void handleAuthenticationEvent(AuthenticationEvent event) {
        switch (event.eventType()) {
            case LOGIN_SUCCESS -> auditService.logAuthenticationAttempt(event.username(), true, event.ipAddress());
            case LOGIN_FAILURE -> auditService.logAuthenticationAttempt(event.username(), false, event.ipAddress());
            case SUSPICIOUS_ACTIVITY -> auditService.logAccountAction(event.userId(),
                SecurityAuditAction.ACCESS_DENIED, event.details(), event.ipAddress());
        }
    }
}

