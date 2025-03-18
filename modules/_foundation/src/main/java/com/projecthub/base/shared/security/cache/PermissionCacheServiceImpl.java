package com.projecthub.base.shared.security.cache;

import com.projecthub.base.shared.security.permission.Permission;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.directory.fortress.core.model.Session;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Two-level caching service for permission checks with garbage collection.
 */
@Service
@Profile("!dev")
@RequiredArgsConstructor
@Slf4j
public class PermissionCacheServiceImpl implements PermissionCacheService, DisposableBean {
    private static final String CACHE_NAME = "permission-cache";
    private static final Duration CACHE_TTL = Duration.ofMinutes(15L);
    private static final Duration GC_INTERVAL = Duration.ofMinutes(5L);
    private static final int INITIAL_CAPACITY = 10000;
    private static final float LOAD_FACTOR = 0.75f;

    private static final double HIGH_MEMORY_THRESHOLD = 0.85; // 85% memory usage triggers aggressive GC
    private static final Duration AGGRESSIVE_GC_INTERVAL = Duration.ofMinutes(1L);
    private final Runtime runtime = Runtime.getRuntime();

    private final CacheManager cacheManager;
    private final ScheduledExecutorService gcExecutor = Executors.newSingleThreadScheduledExecutor();

    // L1 cache with initial capacity and load factor tuned for performance
    private final Map<String, CachedPermission> localCache = new ConcurrentHashMap<>(
        INITIAL_CAPACITY, LOAD_FACTOR);

    @PostConstruct
    public void init() {
// Schedule adaptive garbage collection
        gcExecutor.scheduleWithFixedDelay(
            this::adaptiveGarbageCollection,
            GC_INTERVAL.toMinutes(),
            GC_INTERVAL.toMinutes(),
            TimeUnit.MINUTES
        );

        log.info("Initialized permission cache with adaptive GC");
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

        localCache.clear();
        log.info("Permission cache service shutdown completed");
/**
     * Get a cached permission check result if available
     */
    }

/**
     * Get a cached permission check result if available
     */
    @Override
    public Optional<Boolean> getFromCache(Session session, Permission... permissions) {
        String cacheKey = buildCacheKey(session, permissions);

        // Check L1 cache first
        CachedPermission localResult = localCache.get(cacheKey);
        if (localResult != null && !localResult.isExpired()) {
            log.debug("L1 cache hit for key: {}", cacheKey);
            return Optional.of(Boolean.valueOf(localResult.isGranted()));
        }

        // Check L2 cache
        Cache.ValueWrapper cached = cacheManager.getCache(CACHE_NAME).get(cacheKey);
        if (cached != null) {
            log.debug("L2 cache hit for key: {}", cacheKey);
            Boolean result = (Boolean) cached.get();
            // Populate L1 cache
            localCache.put(cacheKey, new CachedPermission(result));
            return Optional.of(result);
        }

        return Optional.empty();
    }

/**
     * Cache a permission check result
     */
    @Override
    public void cache(Session session, Permission permission, boolean result) {
        String cacheKey = buildCacheKey(session, permission);

        // Update both cache levels
        localCache.put(cacheKey, new CachedPermission(result));
        cacheManager.getCache(CACHE_NAME).put(cacheKey, Boolean.valueOf(result));

        log.debug("Cached permission result: {} = {}", cacheKey, Boolean.valueOf(result));
    }

/**
     * Clear cache entries for a session
     */
    @Override
    public void invalidateSession(Session session) {
        // Clear relevant entries from both cache levels
        String sessionPrefix = session.getSessionId() + ":";
        localCache.keySet().removeIf(key -> key.startsWith(sessionPrefix));
        // Note: Distributed cache entries will expire naturally
        log.debug("Invalidated cache entries for session: {}", session.getSessionId());
    }

/**
     * Perform garbage collection on the local cache
     */
    private void collectGarbage() {
        int sizeBefore = localCache.size();

        // Remove expired entries
        localCache.entrySet().removeIf(entry -> entry.getValue().isExpired());

        int sizeAfter = localCache.size();
        int removedCount = sizeBefore - sizeAfter;

        if (removedCount > 0) {
            log.debug("Cache GC removed {} expired entries. Current size: {}",
                Integer.valueOf(removedCount), Integer.valueOf(sizeAfter));
        }
    }

    /**
     * Perform garbage collection with adaptive frequency based on memory pressure
     */
    private void adaptiveGarbageCollection() {
        try {
            double memoryUsage = getMemoryUsage();
            boolean isHighMemory = memoryUsage > HIGH_MEMORY_THRESHOLD;

            if (isHighMemory) {
                log.warn("High memory usage detected ({}%), performing aggressive cache cleanup",
                    Long.valueOf(Math.round(memoryUsage * 100.0)));

                // More aggressive cleanup when memory is tight
                localCache.clear();
                System.gc(); // Hint to JVM that now would be a good time to GC

                // Schedule next GC sooner
                gcExecutor.schedule(
                    this::adaptiveGarbageCollection,
                    AGGRESSIVE_GC_INTERVAL.toMinutes(),
                    TimeUnit.MINUTES
                );
            } else {
// Normal GC - just remove expired entries
                collectGarbage();
            }

            CacheStats stats = getStats();
            log.debug("Cache stats after GC - Total: {}, Active: {}, Memory Usage: {}%",
                Integer.valueOf(stats.getTotalEntries()),
                Integer.valueOf(stats.getActiveEntries()),
                Long.valueOf(Math.round(getMemoryUsage() * 100.0)));

        } catch (RuntimeException e) {
            log.error("Error during adaptive garbage collection: {}", e.getMessage());
        }
    }

    private double getMemoryUsage() {
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        return (double) usedMemory / (double) maxMemory;
    }

/**
     * Get current cache statistics
     */
    public CacheStats getStats() {
        return new CacheStats(
            localCache.size(),
            localCache.values().stream().filter(CachedPermission::isExpired).count()
        );
    }

    private static String buildCacheKey(Session session, Permission... permissions) {
        StringBuilder key = new StringBuilder(session.getSessionId()).append(":");
        for (Permission permission : permissions) {
            key.append(permission.getResourceType())
               .append(":")
               .append(permission.getOperation())
               .append(":")
               .append(permission.getResourceId())
               .append("|");
        }
        return key.toString();
    }

    private static class CachedPermission {
        private final boolean granted;
        private final long timestamp;
        private final long ttlMillis;

        CachedPermission(boolean granted) {
            this.granted = granted;
            this.timestamp = System.currentTimeMillis();
            this.ttlMillis = CACHE_TTL.toMillis();
        }

        boolean isGranted() {
            return granted;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > ttlMillis;
        }
    }

    public static class CacheStats {
        private final int totalEntries;
        private final long expiredEntries;

        CacheStats(int totalEntries, long expiredEntries) {
            this.totalEntries = totalEntries;
            this.expiredEntries = expiredEntries;
        }

        public int getTotalEntries() {
            return totalEntries;
        }

        public long getExpiredEntries() {
            return expiredEntries;
        }

        public int getActiveEntries() {
            return totalEntries - (int)expiredEntries;
        }
    }
}
