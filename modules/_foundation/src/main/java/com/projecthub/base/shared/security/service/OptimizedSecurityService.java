package com.projecthub.base.shared.security.service;

import com.projecthub.base.shared.security.cache.PermissionCacheService;
import com.projecthub.base.shared.security.permission.CompositePermission;
import com.projecthub.base.shared.security.permission.Permission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.model.Session;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Optimized security service that implements caching, batching, and async permission checks.
 * Core security service that other module-specific security services should use.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OptimizedSecurityService {
    private final AccessMgr accessManager;
    private final SessionService sessionService; // Changed to use our new interface
    private final PermissionCacheService cacheService;
    private final BatchPermissionChecker batchChecker;

    /**
     * Check if the current session has all the specified permissions
     */
    public boolean hasPermissions(Session session, Permission... permissions) {
        // Try cache first
        Optional<Boolean> cachedResult = cacheService.getFromCache(session, permissions);
        if (cachedResult.isPresent()) {
            return cachedResult.get();
        }

        // Check permissions in batch
        Map<Permission, Boolean> results = batchChecker.checkPermissions(
            session,
            Set.of(permissions)
        );

        // Cache results
        results.forEach((p, r) -> cacheService.cache(session, p, r));

        // All permissions must be granted
        return results.values().stream().allMatch(Boolean::booleanValue);
    }

    /**
     * Enforce that the current session has all specified permissions
     */
    public void enforcePermissions(Session session, String message, Permission... permissions) {
        if (!hasPermissions(session, permissions)) {
            throw new AccessDeniedException(message);
        }
    }

    /**
     * Check a composite permission, handling the underlying permission logic
     */
    public boolean hasCompositePermission(Session session, CompositePermission permission) {
        // For composite permissions, check constituent permissions based on allRequired flag
        boolean allRequired = permission.allRequired();
        return permission.getSubPermissions().stream()
            .map(p -> Boolean.valueOf(hasPermission(session, p)))
            .reduce(Boolean.valueOf(allRequired), (a, b) -> Boolean.valueOf(allRequired ? a && b : a || b));
    }

    /**
     * Check a single permission
     */
    public boolean hasPermission(Session session, Permission permission) {
        // Handle composite permissions
        if (permission instanceof CompositePermission) {
            return hasCompositePermission(session, (CompositePermission) permission);
        }

        return hasPermissions(session, permission);
    }

    /**
     * Asynchronously preload permissions for faster subsequent checks
     */
    @Async
    public CompletableFuture<Void> preloadPermissions(String resourceType) {
        return CompletableFuture.runAsync(() -> {
            try {
                batchChecker.preloadPermissions(resourceType);
            } catch (RuntimeException e) {
                log.warn("Failed to preload permissions for resource type {}: {}",
                    resourceType, e.getMessage());
            }
        });
    }

    /**
     * Invalidate cached permissions for a session
     */
    public void invalidateSessionPermissions(Session session) {
        cacheService.invalidateSession(session);
    }
}
