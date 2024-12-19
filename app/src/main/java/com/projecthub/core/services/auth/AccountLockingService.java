package com.projecthub.core.services.auth;

import com.projecthub.core.models.AppUser;
import com.projecthub.core.models.SecurityAuditAction;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;
import com.projecthub.core.services.audit.SecurityAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class responsible for managing account locking mechanisms.
 * It handles incrementing failed login attempts, locking/unlocking accounts,
 * and resetting failed attempts. Additionally, it logs security-related
 * actions for auditing purposes.
 */
@Service
public class AccountLockingService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final AppUserJpaRepository userRepository;
    private final AccountLockingService self;
    private final SecurityAuditService auditService;

    /**
     * Constructs an instance of {@link AccountLockingService}.
     *
     * @param userRepository the repository for accessing user data
     * @param self           a proxy of {@link AccountLockingService} to enable transactional method calls
     * @param auditService   the service responsible for logging security audit actions
     */
    @Autowired
    public AccountLockingService(AppUserJpaRepository userRepository, AccountLockingService self, SecurityAuditService auditService) {
        this.userRepository = userRepository;
        this.self = self;
        this.auditService = auditService;
    }

    /**
     * Increments the number of failed login attempts for the specified user.
     * If the number of failed attempts reaches the maximum threshold, the account is locked.
     *
     * @param user the {@link AppUser} whose failed attempts are to be incremented
     */
    @Transactional
    public void incrementFailedAttempts(AppUser user) {
        user.setFailedAttempts(user.getFailedAttempts() + 1);
        if (user.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
            self.lockAccount(user);
        }
        userRepository.save(user);
    }

    /**
     * Locks the specified user's account, preventing further login attempts.
     * Also logs the account locking action for auditing purposes.
     *
     * @param user the {@link AppUser} whose account is to be locked
     */
    @Transactional
    public void lockAccount(AppUser user) {
        user.setAccountNonLocked(false);
        userRepository.save(user);
        auditService.logAccountAction(user.getId(), SecurityAuditAction.ACCOUNT_LOCKED, "Too many failed attempts");
    }

    /**
     * Unlocks the specified user's account, allowing them to attempt logging in again.
     * Resets the failed login attempts count and logs the account unlocking action.
     *
     * @param user the {@link AppUser} whose account is to be unlocked
     */
    @Transactional
    public void unlockAccount(AppUser user) {
        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
        userRepository.save(user);
        auditService.logAccountAction(user.getId(), SecurityAuditAction.ACCOUNT_UNLOCKED, "Account manually unlocked");
    }

    /**
     * Resets the failed login attempts count for the specified user without locking the account.
     *
     * @param user the {@link AppUser} whose failed attempts are to be reset
     */
    @Transactional
    public void resetFailedAttempts(AppUser user) {
        user.setFailedAttempts(0);
        userRepository.save(user);
    }
}