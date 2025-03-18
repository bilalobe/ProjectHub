package com.projecthub.base.shared.security.service;

import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.model.Permission;
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

import java.util.Map;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class FortressBatchPermissionCheckerTest {

    @Mock
    private AccessMgr accessManager;

    @Mock
    private Session session;

    @InjectMocks
    private FortressBatchPermissionChecker permissionChecker;

    private com.projecthub.base.shared.security.permission.Permission testPermission;

    FortressBatchPermissionCheckerTest() {
    }

    @BeforeEach
    void setUp() {
        testPermission = createTestPermission("test", "read", "123");
    }

    @Test
    void checkPermissions_whenPreloaded_usesPreloadedPermissions() throws Exception {
        // Arrange
        Set<Permission> preloadedPermissions = Set.of(
            new Permission("test", "read")
        );
        Mockito.when(accessManager.permissionRoles("test")).thenReturn(preloadedPermissions);
        permissionChecker.preloadPermissions("test");

        // Act
        Map<com.projecthub.base.shared.security.permission.Permission, Boolean> results =
            permissionChecker.checkPermissions(session, Set.of(testPermission));

        // Assert
        Assertions.assertTrue(results.get(testPermission));
        Mockito.verify(accessManager, Mockito.never()).checkAccess(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void checkPermissions_whenNotPreloaded_checksWithFortress() throws Exception {
        // Arrange
        Mockito.when(accessManager.checkAccess(ArgumentMatchers.eq(session), ArgumentMatchers.any(Permission.class))).thenReturn(true);

        // Act
        Map<com.projecthub.base.shared.security.permission.Permission, Boolean> results =
            permissionChecker.checkPermissions(session, Set.of(testPermission));

        // Assert
        Assertions.assertTrue(results.get(testPermission));
        Mockito.verify(accessManager).checkAccess(ArgumentMatchers.eq(session), ArgumentMatchers.any(Permission.class));
    }

    @Test
    void checkPermissions_whenFortressThrowsException_returnsFalse() throws Exception {
        // Arrange
        Mockito.when(accessManager.checkAccess(ArgumentMatchers.eq(session), ArgumentMatchers.any(Permission.class)))
            .thenThrow(new RuntimeException("Fortress error"));

        // Act
        Map<com.projecthub.base.shared.security.permission.Permission, Boolean> results =
            permissionChecker.checkPermissions(session, Set.of(testPermission));

        // Assert
        Assertions.assertFalse(results.get(testPermission));
    }

    @Test
    void preloadPermissions_whenSuccessful_cachesPermissions() throws Exception {
        // Arrange
        Set<Permission> permissions = Set.of(
            new Permission("test", "read"),
            new Permission("test", "write")
        );
        Mockito.when(accessManager.permissionRoles("test")).thenReturn(permissions);

        // Act
        permissionChecker.preloadPermissions("test");

        // Create permissions to check against preloaded cache
        com.projecthub.base.shared.security.permission.Permission readPerm =
            createTestPermission("test", "read", null);
        com.projecthub.base.shared.security.permission.Permission writePerm =
            createTestPermission("test", "write", null);

        Map<com.projecthub.base.shared.security.permission.Permission, Boolean> results =
            permissionChecker.checkPermissions(session, Set.of(readPerm, writePerm));

        // Assert
        Assertions.assertTrue(results.get(readPerm));
        Assertions.assertTrue(results.get(writePerm));
        Mockito.verify(accessManager, Mockito.never()).checkAccess(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void clearPreloadedPermissions_removesPermissionsFromCache() throws Exception {
        // Arrange
        Set<Permission> permissions = Set.of(new Permission("test", "read"));
        Mockito.when(accessManager.permissionRoles("test")).thenReturn(permissions);
        permissionChecker.preloadPermissions("test");

        // Act
        permissionChecker.clearPreloadedPermissions("test");

        // Now should fall back to Fortress check
        Mockito.when(accessManager.checkAccess(ArgumentMatchers.eq(session), ArgumentMatchers.any(Permission.class))).thenReturn(true);
        Map<com.projecthub.base.shared.security.permission.Permission, Boolean> results =
            permissionChecker.checkPermissions(session, Set.of(testPermission));

        // Assert
        Assertions.assertTrue(results.get(testPermission));
        Mockito.verify(accessManager).checkAccess(ArgumentMatchers.eq(session), ArgumentMatchers.any(Permission.class));
    }

    private com.projecthub.base.shared.security.permission.Permission createTestPermission(
            String resourceType, String operation, String resourceId) {
        return new com.projecthub.base.shared.security.permission.Permission() {
            @Override
            public String getResourceType() { return resourceType; }
            @Override
            public String getOperation() { return operation; }
            @Override
            public boolean requiresOwnership() { return false; }
            @Override
            public boolean isAdministrative() { return false; }
            @Override
            public String getResourceId() { return resourceId; }
        };
    }
}
