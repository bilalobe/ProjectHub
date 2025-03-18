package com.projecthub.base.shared.security.monitoring;

import com.projecthub.base.shared.security.cache.PermissionCacheService;
import com.projecthub.base.shared.security.service.FortressBatchPermissionChecker;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class PermissionCacheMetrics {

    private final MeterRegistry registry;
    private final PermissionCacheService cacheService;
    private final FortressBatchPermissionChecker batchChecker;

    private Timer permissionCheckTimer = null;

    @PostConstruct
    public void init() {
        // Cache size metrics
        Gauge.builder("security.permissions.cache.size",
                cacheService,
                service -> (double) service.getStats().getTotalEntries())
            .description("Total number of cached permission entries")
            .tag("type", "total")
            .register(registry);

        Gauge.builder("security.permissions.cache.size",
                cacheService,
                service -> (double) service.getStats().getActiveEntries())
            .description("Number of active cached permission entries")
            .tag("type", "active")
            .register(registry);

        Gauge.builder("security.permissions.cache.size",
                cacheService,
                service -> (double) service.getStats().getExpiredEntries())
            .description("Number of expired cached permission entries")
            .tag("type", "expired")
            .register(registry);

        // Preload metrics
        Gauge.builder("security.permissions.preload.types",
                batchChecker,
                checker -> (double) checker.getStats().getTotalResourceTypes())
            .description("Total number of preloaded resource types")
            .register(registry);

        Gauge.builder("security.permissions.preload.permissions",
                batchChecker,
                checker -> (double) checker.getStats().getTotalPermissions())
            .description("Total number of preloaded permissions")
            .register(registry);

        // Performance metrics
        permissionCheckTimer = Timer.builder("security.permissions.check.time")
            .description("Time taken to check permissions")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(registry);
    }

    /**
     * Record the time taken for a permission check
     */
    public void recordPermissionCheckTime(long millisTaken) {
        permissionCheckTimer.record(millisTaken, TimeUnit.MILLISECONDS);
    }
}
