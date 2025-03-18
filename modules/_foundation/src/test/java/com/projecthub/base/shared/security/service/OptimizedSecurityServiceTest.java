package com.projecthub.base.shared.security.service;

import com.projecthub.base.shared.security.cache.PermissionCacheService;
import com.projecthub.base.shared.security.permission.CompositePermission;
import com.projecthub.base.shared.security.permission.Permission;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.model.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OptimizedSecurityServiceTest {

    @Mock
    private AccessMgr accessManager;

    @Mock
    private SessionService sessionService;

    @Mock
    private PermissionCacheService cacheService;

    @Mock
    private BatchPermissionChecker batchChecker;

    @Mock
    private Session session;

    private OptimizedSecurityService securityService;

    @BeforeEach
    public void setup() {
        securityService = new OptimizedSecurityService(
            accessManager,
            sessionService,
            cacheService,
            batchChecker
        );
    }

    @Test
    public void testHasPermission_whenPermissionGranted_returnsTrue() {
        // Given
        Permission testPermission = createTestPermission("resource", "read", "123", false, false);

        // Mock cache miss
        when(cacheService.getFromCache(any(), any(Permission[].class)))
            .thenReturn(Optional.empty());

        // Mock batch permission check to return true
        Map<Permission, Boolean> results = new HashMap<>();
        results.put(testPermission, Boolean.TRUE);
        when(batchChecker.checkPermissions(any(Session.class), anySet()))
            .thenReturn(results);

        // When
        boolean result = securityService.hasPermission(session, testPermission);

        // Then
        assertTrue(result);
    }

    @Test
    public void testHasPermission_whenPermissionDenied_returnsFalse() {
        // Given
        Permission testPermission = createTestPermission("restricted", "delete", "456", true, true);

        // Mock cache miss
        when(cacheService.getFromCache(any(), any(Permission[].class)))
            .thenReturn(Optional.empty());

        // Mock batch permission check to return false
        Map<Permission, Boolean> results = new HashMap<>();
        results.put(testPermission, Boolean.FALSE);
        when(batchChecker.checkPermissions(any(Session.class), anySet()))
            .thenReturn(results);

        // When
        boolean result = securityService.hasPermission(session, testPermission);

        // Then
        assertFalse(result);
    }

    @Test
    public void testHasPermission_whenPermissionInCache_returnsCachedValue() {
        // Given
        Permission testPermission = createTestPermission("cached", "view", "789", false, false);

        // Mock cache hit
        when(cacheService.getFromCache(any(), any(Permission[].class)))
            .thenReturn(Optional.of(Boolean.TRUE));

        // When
        boolean result = securityService.hasPermission(session, testPermission);

        // Then
        assertTrue(result);
    }

    @Test
    public void testHasCompositePermission_whenAllRequired_andAllGranted_returnsTrue() {
        // Given
        CompositePermission compositePermission = createTestCompositePermission(true);

        // Mock permissions being granted
        for (Permission p : compositePermission.getSubPermissions()) {
            Map<Permission, Boolean> results = new HashMap<>();
            results.put(p, Boolean.TRUE);
            when(batchChecker.checkPermissions(any(Session.class), Set.of(p)))
                .thenReturn(results);
        }

        // When
        boolean result = securityService.hasCompositePermission(session, compositePermission);

        // Then
        assertTrue(result);
    }

    private Permission createTestPermission(String resourceType, String operation,
                                           String resourceId, boolean requiresOwnership,
                                           boolean isAdministrative) {
        return new Permission() {
            @Override
            public String getResourceType() { return resourceType; }
            @Override
            public String getOperation() { return operation; }
            @Override
            public boolean requiresOwnership() { return requiresOwnership; }
            @Override
            public boolean isAdministrative() { return isAdministrative; }
            @Override
            public String getResourceId() { return resourceId; }
        };
    }

    private CompositePermission createTestCompositePermission(boolean allRequired) {
        return new CompositePermission() {
            @Override
            public List<Permission> getSubPermissions() {
                return List.of(
                    createTestPermission("test", "read", "123", false, false),
                    createTestPermission("test", "write", "123", false, false)
                );
            }
            @Override
            public String getResourceType() { return "test"; }
            @Override
            public String getOperation() { return "manage"; }
            @Override
            public boolean requiresOwnership() { return false; }
            @Override
            public boolean isAdministrative() { return true; }
            @Override
            public String getResourceId() { return "123"; }
            @Override
            public boolean allRequired() { return allRequired; }
        };
    }
}
