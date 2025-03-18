package com.projecthub.base.shared.security.service;

import com.projecthub.base.shared.security.permission.Permission;
import lombok.extern.slf4j.Slf4j;
import org.apache.directory.fortress.core.model.Session;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Development mode implementation of BatchPermissionChecker.
 * Always grants permissions unless they are administrative or require ownership.
 */
@Service
@Profile("!auth")
@Slf4j
public class DevModeBatchPermissionChecker implements BatchPermissionChecker {
    
    private final Map<String, Set<String>> preloadedPermissions = new ConcurrentHashMap<>();
    
    @Override
    public Map<Permission, Boolean> checkPermissions(Session session, Set<Permission> permissions) {
        Map<Permission, Boolean> results = new HashMap<>();
        
        for (Permission permission : permissions) {
            // In dev mode, only restrict administrative permissions
            boolean granted = !permission.isAdministrative() && 
                            (!permission.requiresOwnership() || isOwner(session, permission));
            
            results.put(permission, granted);
            
            log.debug("Dev mode permission check: {} -> {} for session {}",
                     permission, granted, session.getSessionId());
        }
        
        return results;
    }
    
    @Override
    public void preloadPermissions(String resourceType) {
        log.debug("Dev mode preload permissions for resource type: {}", resourceType);
        // No-op in dev mode
    }
    
    private boolean isOwner(Session session, Permission permission) {
        // In dev mode, consider the user an owner if they're authenticated
        return session.isAuthenticated() && permission.getResourceId() != null;
    }
}