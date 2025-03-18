package com.projecthub.base.shared.security.cache;

import com.projecthub.base.shared.security.permission.Permission;
import org.apache.directory.fortress.core.model.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PermissionCacheServiceTest {

    private static final String CACHE_NAME = "permission-cache";

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @Mock
    private Session session;

    @Mock
    private Cache.ValueWrapper valueWrapper;

    private PermissionCacheService cacheService;
    private Permission testPermission;

    PermissionCacheServiceTest() {
    }

    @BeforeEach
    void setUp() {
        Mockito.when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);
        Mockito.when(session.getSessionId()).thenReturn("test-session");

        cacheService = new PermissionCacheService(cacheManager);
        testPermission = createTestPermission("test", "read", "123");
    }

    @Test
    void getFromCache_whenL1CacheHit_returnsFromL1() {
        // Arrange
        cacheService.cache(session, testPermission, true);

        // Act
        Optional<Boolean> result = cacheService.getFromCache(session, testPermission);

        // Assert
        Assertions.assertTrue(result.isPresent());
        Assertions.assertTrue(result.get());
        Mockito.verify(cache, Mockito.never()).get(ArgumentMatchers.any()); // Should not check L2 cache
    }

    @Test
    void getFromCache_whenL1CacheMissL2CacheHit_returnsFromL2AndPopulatesL1() {
        // Arrange
        Mockito.when(cache.get(ArgumentMatchers.anyString())).thenReturn(valueWrapper);
        Mockito.when(valueWrapper.get()).thenReturn(Boolean.TRUE);

        // Act
        Optional<Boolean> result = cacheService.getFromCache(session, testPermission);
        // Second call should hit L1
        Optional<Boolean> secondResult = cacheService.getFromCache(session, testPermission);

        // Assert
        Assertions.assertTrue(result.isPresent());
        Assertions.assertTrue(result.get());
        Assertions.assertTrue(secondResult.isPresent());
        Assertions.assertTrue(secondResult.get());
        Mockito.verify(cache, Mockito.times(1)).get(ArgumentMatchers.anyString()); // Should only check L2 once
    }

    @Test
    void getFromCache_whenNoCacheHit_returnsEmpty() {
        // Arrange
        Mockito.when(cache.get(ArgumentMatchers.anyString())).thenReturn(null);

        // Act
        Optional<Boolean> result = cacheService.getFromCache(session, testPermission);

        // Assert
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void cache_storesInBothCacheLevels() {
        // Act
        cacheService.cache(session, testPermission, true);

        // Assert - Check L2 cache was updated
        Mockito.verify(cache).put(ArgumentMatchers.anyString(), Boolean.valueOf(ArgumentMatchers.eq(true)));

        // Check L1 cache was updated by doing a read
        Optional<Boolean> result = cacheService.getFromCache(session, testPermission);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertTrue(result.get());
        Mockito.verify(cache, Mockito.never()).get(ArgumentMatchers.any()); // Should read from L1
    }

    @Test
    void invalidateSession_clearsBothCacheLevels() {
        // Arrange
        cacheService.cache(session, testPermission, true);

        // Act
        cacheService.invalidateSession(session);

        // Assert - L1 cache should be cleared
        Optional<Boolean> result = cacheService.getFromCache(session, testPermission);
        Assertions.assertTrue(result.isEmpty());
    }

    private Permission createTestPermission(String resourceType, String operation, String resourceId) {
        return new Permission() {
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
