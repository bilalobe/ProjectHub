package com.projecthub.base.team.infrastructure.security;

import com.projecthub.base.auth.service.fortress.FortressSessionService;
import com.projecthub.base.shared.config.RbacSecurityConfig;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.model.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TeamSecurityServiceTest {

    @Mock
    private AccessMgr accessManager;

    @Mock
    private FortressSessionService sessionService;

    @Mock
    private RbacSecurityConfig securityConfig;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Session fortressSession;

    @InjectMocks
    private TeamSecurityService teamSecurityService;

    private UUID testTeamId;

    TeamSecurityServiceTest() {
    }

    @BeforeEach
    void setUp() {
        testTeamId = UUID.randomUUID();

        // Setup Security Context
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Setup Session Service
        Mockito.when(sessionService.getSession(ArgumentMatchers.any(Authentication.class))).thenReturn(fortressSession);
    }

    @Test
    void enforceCreatePermission_withValidPermission_succeeds() {
        // Arrange
        Mockito.when(Boolean.valueOf(securityConfig.hasPermission(ArgumentMatchers.eq(fortressSession),
                        ArgumentMatchers.eq(TeamSecurityConfig.ObjectIdentifiers.TEAM),
                        ArgumentMatchers.eq(TeamSecurityConfig.Operations.CREATE))))
            .thenReturn(Boolean.TRUE);

        // Act & Assert - No exception should be thrown
        Assertions.assertDoesNotThrow(() -> teamSecurityService.enforceCreatePermission());
    }

    @Test
    void enforceCreatePermission_withoutPermission_throwsAccessDeniedException() {
        // Arrange
        Mockito.when(Boolean.valueOf(securityConfig.hasPermission(ArgumentMatchers.eq(fortressSession),
                        ArgumentMatchers.eq(TeamSecurityConfig.ObjectIdentifiers.TEAM),
                        ArgumentMatchers.eq(TeamSecurityConfig.Operations.CREATE))))
            .thenReturn(Boolean.FALSE);

        // Act & Assert
        Assertions.assertThrows(AccessDeniedException.class, () -> teamSecurityService.enforceCreatePermission());
    }

    @Test
    void enforceReadPermission_withValidPermission_succeeds() {
        // Arrange
        Mockito.when(Boolean.valueOf(securityConfig.hasPermission(ArgumentMatchers.eq(fortressSession),
                        ArgumentMatchers.eq(TeamSecurityConfig.ObjectIdentifiers.TEAM),
                        ArgumentMatchers.eq(TeamSecurityConfig.Operations.READ))))
            .thenReturn(Boolean.TRUE);

        // Act & Assert - No exception should be thrown
        Assertions.assertDoesNotThrow(() -> teamSecurityService.enforceReadPermission(testTeamId));
    }

    @Test
    void enforceUpdatePermission_withValidPermission_succeeds() {
        // Arrange
        Mockito.when(Boolean.valueOf(securityConfig.hasPermission(ArgumentMatchers.eq(fortressSession),
                        ArgumentMatchers.eq(TeamSecurityConfig.ObjectIdentifiers.TEAM),
                        ArgumentMatchers.eq(TeamSecurityConfig.Operations.UPDATE))))
            .thenReturn(Boolean.TRUE);

        // Act & Assert - No exception should be thrown
        Assertions.assertDoesNotThrow(() -> teamSecurityService.enforceUpdatePermission(testTeamId));
    }

    @Test
    void enforceDeletePermission_withValidPermission_succeeds() {
        // Arrange
        Mockito.when(Boolean.valueOf(securityConfig.hasPermission(ArgumentMatchers.eq(fortressSession),
                        ArgumentMatchers.eq(TeamSecurityConfig.ObjectIdentifiers.TEAM),
                        ArgumentMatchers.eq(TeamSecurityConfig.Operations.DELETE))))
            .thenReturn(Boolean.TRUE);

        // Act & Assert - No exception should be thrown
        Assertions.assertDoesNotThrow(() -> teamSecurityService.enforceDeletePermission(testTeamId));
    }

    @Test
    void enforceAddMemberPermission_withValidPermission_succeeds() {
        // Arrange
        Mockito.when(Boolean.valueOf(securityConfig.hasPermission(ArgumentMatchers.eq(fortressSession),
                        ArgumentMatchers.eq(TeamSecurityConfig.ObjectIdentifiers.TEAM_MEMBERSHIP),
                        ArgumentMatchers.eq(TeamSecurityConfig.Operations.ADD_MEMBER))))
            .thenReturn(Boolean.TRUE);

        // Act & Assert - No exception should be thrown
        Assertions.assertDoesNotThrow(() -> teamSecurityService.enforceAddMemberPermission(testTeamId));
    }

    @Test
    void getCurrentSession_withNoAuthentication_throwsAccessDeniedException() {
        // Arrange
        Mockito.when(securityContext.getAuthentication()).thenReturn(null);

        // Act & Assert
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            teamSecurityService.enforceReadPermission(testTeamId);
        });
    }
}
