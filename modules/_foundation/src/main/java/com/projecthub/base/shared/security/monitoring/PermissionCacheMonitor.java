package com.projecthub.base.shared.security.monitoring;

import com.projecthub.base.shared.security.cache.PermissionCacheService;
import com.projecthub.base.shared.security.service.FortressBatchPermissionChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource(
    objectName = "com.projecthub:type=Security,name=PermissionCacheMonitor",
    description = "Permission Cache Monitoring and Management"
)
@RequiredArgsConstructor
public class PermissionCacheMonitor {

    private final PermissionCacheService cacheService;
    private final FortressBatchPermissionChecker batchChecker;

    @ManagedAttribute(description = "Total number of cached permission entries")
    public int getTotalCacheEntries() {
        return cacheService.getStats().getTotalEntries();
    }

    @ManagedAttribute(description = "Number of active (non-expired) cache entries")
    public int getActiveCacheEntries() {
        return cacheService.getStats().getActiveEntries();
    }

    @ManagedAttribute(description = "Number of expired cache entries pending cleanup")
    public long getExpiredCacheEntries() {
        return cacheService.getStats().getExpiredEntries();
    }

    @ManagedAttribute(description = "Total number of preloaded resource types")
    public int getTotalPreloadedTypes() {
        return batchChecker.getStats().getTotalResourceTypes();
    }

    @ManagedAttribute(description = "Number of active preloaded resource types")
    public int getActivePreloadedTypes() {
        return batchChecker.getStats().getActiveResourceTypes();
    }

    @ManagedAttribute(description = "Total number of preloaded permissions across all types")
    public int getTotalPreloadedPermissions() {
        return batchChecker.getStats().getTotalPermissions();
    }

    @ManagedOperation(description = "Force garbage collection of expired cache entries")
    public void forceGarbageCollection() {
        // These methods will be called by JMX
        cacheService.invalidateSession(null); // Special case to clear all
    }

    @ManagedOperation(description = "Clear all preloaded permissions")
    public void clearAllPreloadedPermissions() {
        for (String resourceType : getResourceTypes()) {
            batchChecker.clearPreloadedPermissions(resourceType);
        }
    }

    private static String[] getResourceTypes() {
        return new String[] {
            "school", "cohort", "project", "team", "task",
            "milestone", "submission", "student"
        };
    }
}
