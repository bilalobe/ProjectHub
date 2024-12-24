package com.projecthub.core.services.auth;

import com.projecthub.core.entities.AppUser;
import com.projecthub.core.enums.SecurityAuditAction;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;
import com.projecthub.core.services.audit.SecurityAuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountLockingService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final AppUserJpaRepository userRepository;
    private final AccountLockingService self;
    private final SecurityAuditService auditService;

    public AccountLockingService(
            AppUserJpaRepository userRepository,
            AccountLockingService self,
            SecurityAuditService auditService
    ) {
        this.userRepository = userRepository;
        this.self = self;
        this.auditService = auditService;
    }

    @Transactional
    public void incrementFailedAttempts(AppUser user) {
        int attempts = user.incrementFailedAttempts();
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            self.lockAccount(user);
        }
        userRepository.save(user);
    }

    @Transactional
    public void lockAccount(AppUser user) {
        user.setLocked(true);
        userRepository.save(user);
        auditService.logAccountAction(user.getId(), SecurityAuditAction.ACCOUNT_LOCKED,
                "Account locked after " + MAX_FAILED_ATTEMPTS + " failed attempts");
    }

    @Transactional
    public void unlockAccount(AppUser user) {
        user.setLocked(false);
        user.resetFailedAttempts();
        userRepository.save(user);
        auditService.logAccountAction(user.getId(), SecurityAuditAction.ACCOUNT_UNLOCKED,
                "Account manually unlocked");
    }

    @Transactional
    public void resetFailedAttempts(AppUser user) {
        user.resetFailedAttempts();
        userRepository.save(user);
    }
}
