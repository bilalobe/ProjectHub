package com.projecthub.base.shared.security.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.model.Permission;
import org.apache.directory.fortress.core.model.Session;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Apache Fortress specific implementation of batch permission checking.
 * Implements caching and batching optimizations while working with Fortress RBAC.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FortressBatchPermissionChecker implements BatchPermissionChecker, DisposableBean {

    private static final Duration PRELOAD_TTL = Duration.ofHours(1L);
    private static final Duration GC_INTERVAL = Duration.ofMinutes(15L);
    private static final int MAX_PRELOADED_TYPES = 100;
    private static final double MEMORY_EVICTION_THRESHOLD = 0.80; // 80% memory usage
    private final Runtime runtime = Runtime.getRuntime();

    private final AccessMgr accessManager;
    private final ScheduledExecutorService gcExecutor = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, PreloadedPermissions> preloadedPermissions = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // Schedule periodic garbage collection
        gcExecutor.scheduleAtFixedRate(
            this::collectGarbage,
            GC_INTERVAL.toMinutes(),
            GC_INTERVAL.toMinutes(),
            TimeUnit.MINUTES
        );

        log.info("Initialized batch permission checker with GC interval: {} minutes",
                Long.valueOf(GC_INTERVAL.toMinutes()));
    }

    @Override
    public void destroy() {
        gcExecutor.shutdown();
        try {
            if (!gcExecutor.awaitTermination(60L, TimeUnit.SECONDS)) {
                gcExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            gcExecutor.shutdownNow();
        }

        preloadedPermissions.clear();
        log.info("Batch permission checker shutdown completed");
    }

    @Override
    public Map<com.projecthub.base.shared.security.permission.Permission, Boolean> checkPermissions(
            Session session, Set<com.projecthub.base.shared.security.permission.Permission> permissions) {

        Map<com.projecthub.base.shared.security.permission.Permission, Boolean> results = new HashMap<>();

        try {
            // Convert to Fortress permissions and check in batch
            for (com.projecthub.base.shared.security.permission.Permission p : permissions) {
                Permission fortressPermission = new Permission(p.getResourceType(), p.getOperation());
                if (p.getResourceId() != null) {
                    fortressPermission.setObjId(p.getResourceId());
                }

                // Check against preloaded permissions first
                PreloadedPermissions preloaded = preloadedPermissions.get(p.getResourceType());
                if (preloaded != null && !preloaded.isExpired() &&
                    isPreloadedPermissionGranted(fortressPermission, preloaded.getPermissions())) {
                    results.put(p, Boolean.TRUE);
                    continue;
                }

                // Fall back to Fortress check
                try {
                    boolean hasPermission = accessManager.checkAccess(session, fortressPermission);
                    results.put(p, Boolean.valueOf(hasPermission));
                } catch (RuntimeException e) {
                    log.error("Error checking permission {}: {}", fortressPermission, e.getMessage());
                    results.put(p, Boolean.FALSE);
                }
            }
        } catch (RuntimeException e) {
            log.error("Batch permission check failed: {}", e.getMessage());
            permissions.forEach(p -> {
                if (!results.containsKey(p)) {
                    results.put(p, Boolean.FALSE);
                }
            });
        }

        return results;
    }

    @Override
    public void preloadPermissions(String resourceType) {
        try {
            // Check if we need to evict before adding more
            if (preloadedPermissions.size() >= MAX_PRELOADED_TYPES || isMemoryPressure()) {
                evictLeastRecentlyUsed();
            }

            // Load all permissions for the resource type from Fortress
            Set<Permission> permissions = accessManager.permissionRoles(resourceType);
            preloadedPermissions.put(resourceType, new PreloadedPermissions(permissions));

            log.debug("Preloaded {} permissions for resource type {} (Total types: {})",
                    Integer.valueOf(permissions.size()), resourceType, Integer.valueOf(preloadedPermissions.size()));

        } catch (RuntimeException e) {
            log.error("Failed to preload permissions for {}: {}",
                resourceType, e.getMessage());
        }
    }

    private boolean isMemoryPressure() {
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        return (double) usedMemory / (double) maxMemory > MEMORY_EVICTION_THRESHOLD;
    }

    private void evictLeastRecentlyUsed() {
        // Find and remove the oldest entries until we're under the limit
        while (preloadedPermissions.size() >= MAX_PRELOADED_TYPES || isMemoryPressure()) {
            String oldestType = preloadedPermissions.entrySet().stream()
                .min((a, b) -> Long.compare(
                    a.getValue().getLastAccessTime(),
                    b.getValue().getLastAccessTime()))
                .map(Map.Entry::getKey)
                .orElse(null);

            if (oldestType != null) {
                preloadedPermissions.remove(oldestType);
                log.debug("Evicted preloaded permissions for resource type: {}", oldestType);
            } else {
                break;
            }
        }
    }

    private static boolean isPreloadedPermissionGranted(Permission permission, Collection<Permission> preloadedSet) {
        return preloadedSet.stream()
            .anyMatch(p -> p.getObjName().equals(permission.getObjName())
                && p.getOpName().equals(permission.getOpName())
                && (permission.getObjId() == null
                    || permission.getObjId().equals(p.getObjId())));
    }

    /**
     * Clear preloaded permissions for a resource type
     */
    public void clearPreloadedPermissions(String resourceType) {
        preloadedPermissions.remove(resourceType);
        log.debug("Cleared preloaded permissions for resource type {}", resourceType);
    }

    /**
     * Perform garbage collection on preloaded permissions
     */
    private void collectGarbage() {
        int sizeBefore = preloadedPermissions.size();

        // Remove expired preloaded permissions
        preloadedPermissions.entrySet().removeIf(entry -> entry.getValue().isExpired());

        int sizeAfter = preloadedPermissions.size();
        int removedCount = sizeBefore - sizeAfter;

        if (removedCount > 0) {
            log.debug("GC removed {} expired preloaded permission sets. Current size: {}",
                    Integer.valueOf(removedCount), Integer.valueOf(sizeAfter));
        }
    }

    /**
     * Get current preload cache statistics
     */
    public PreloadStats getStats() {
        return new PreloadStats(
            preloadedPermissions.size(),
            preloadedPermissions.values().stream()
                .filter(PreloadedPermissions::isExpired)
                .count(),
            preloadedPermissions.values().stream()
                .mapToInt(p -> p.getPermissions().size())
                .sum()
        );
    }

    private static class PreloadedPermissions {
        private final Set<Permission> permissions;
        private final long timestamp;
        private final long ttlMillis;
        private volatile long lastAccessTime;

        PreloadedPermissions(Set<Permission> permissions) {
            this.permissions = permissions;
            this.timestamp = System.currentTimeMillis();
            this.ttlMillis = PRELOAD_TTL.toMillis();
            this.lastAccessTime = timestamp;
        }

        Set<Permission> getPermissions() {
            this.lastAccessTime = System.currentTimeMillis();
            return permissions;
        }

        long getLastAccessTime() {
            return lastAccessTime;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > ttlMillis;
        }
    }

    public static class PreloadStats {
        private final int totalResourceTypes;
        private final long expiredResourceTypes;
        private final int totalPermissions;

        PreloadStats(int totalResourceTypes, long expiredResourceTypes, int totalPermissions) {
            this.totalResourceTypes = totalResourceTypes;
            this.expiredResourceTypes = expiredResourceTypes;
            this.totalPermissions = totalPermissions;
        }

        public int getTotalResourceTypes() {
            return totalResourceTypes;
        }

        public long getExpiredResourceTypes() {
            return expiredResourceTypes;
        }

        public int getActiveResourceTypes() {
            return totalResourceTypes - (int)expiredResourceTypes;
        }

        public int getTotalPermissions() {
            return totalPermissions;
        }
    }
}
