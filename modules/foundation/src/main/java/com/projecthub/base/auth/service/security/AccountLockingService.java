package com.projecthub.base.auth.service.security;

import com.projecthub.base.auth.application.security.SecurityAuditService;
import com.projecthub.base.repository.jpa.AppUserJpaRepository;
import com.projecthub.base.user.domain.entity.AppUser;
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
