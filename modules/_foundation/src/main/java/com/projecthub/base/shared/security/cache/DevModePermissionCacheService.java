package com.projecthub.base.shared.security.cache;

import com.projecthub.base.shared.security.permission.Permission;
import lombok.extern.slf4j.Slf4j;
import org.apache.directory.fortress.core.model.Session;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory cache implementation for development mode.
 * Uses a ConcurrentHashMap without expiration or garbage collection.
 */
@Service
@Profile("!auth")
@Slf4j
public class DevModePermissionCacheService implements PermissionCacheService {
    
    private final Map<String, Boolean> cache = new ConcurrentHashMap<>();
    
    @Override
    public Optional<Boolean> getFromCache(Session session, Permission... permissions) {
        String key = buildCacheKey(session, permissions);
        Boolean result = cache.get(key);
        
        if (result != null) {
            log.debug("Dev cache hit for key: {}", key);
        }
        
        return Optional.ofNullable(result);
    }
    
    @Override
    public void cache(Session session, Permission permission, boolean result) {
        String key = buildCacheKey(session, permission);
        cache.put(key, result);
        log.debug("Dev cache store: {} = {}", key, result);
    }
    
    @Override
    public void invalidateSession(Session session) {
        String prefix = session.getSessionId() + ":";
        cache.keySet().removeIf(key -> key.startsWith(prefix));
        log.debug("Dev cache cleared for session: {}", session.getSessionId());
    }
    
    private String buildCacheKey(Session session, Permission... permissions) {
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
}