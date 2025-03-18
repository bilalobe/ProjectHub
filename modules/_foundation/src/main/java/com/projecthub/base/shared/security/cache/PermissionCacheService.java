package com.projecthub.base.shared.security.cache;

import com.projecthub.base.shared.security.permission.Permission;
import org.apache.directory.fortress.core.model.Session;
import java.util.Optional;

/**
 * Service interface for caching permission check results.
 */
public interface PermissionCacheService {
    Optional<Boolean> getFromCache(Session session, Permission... permissions);
    void cache(Session session, Permission permission, boolean result);
    void invalidateSession(Session session);
}
