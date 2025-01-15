package com.projecthub.base.auth.application.security;

import com.projecthub.base.repository.jpa.AppUserJpaRepository;
import com.projecthub.base.shared.domain.enums.security.SecurityAuditAction;
import com.projecthub.base.user.domain.entity.AppUser;
import com.projecthub.base.user.domain.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AccountManagementService {
    private final AppUserJpaRepository userRepository;
    private final SecurityAuditService auditService;

    public AccountManagementService(
        final AppUserJpaRepository userRepository,
        final SecurityAuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional
    public void lockAccount(final UUID userId) {
        final AppUser user = this.userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        user.setLocked(true);
        this.userRepository.save(user);
        this.auditService.logAccountAction(userId, SecurityAuditAction.ACCOUNT_LOCKED);
    }
}
