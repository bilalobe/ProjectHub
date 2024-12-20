package com.projecthub.core.services.account;

import com.projecthub.core.exceptions.UserNotFoundException;
import com.projecthub.core.models.AppUser;
import com.projecthub.core.models.SecurityAuditAction;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;
import com.projecthub.core.services.audit.SecurityAuditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AccountManagementService {
    private final AppUserJpaRepository userRepository;
    private final SecurityAuditService auditService;

    public AccountManagementService(
            AppUserJpaRepository userRepository,
            SecurityAuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional
    public void lockAccount(UUID userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setLocked(true);
        userRepository.save(user);
        auditService.logAccountAction(userId, SecurityAuditAction.ACCOUNT_LOCKED);
    }
}
