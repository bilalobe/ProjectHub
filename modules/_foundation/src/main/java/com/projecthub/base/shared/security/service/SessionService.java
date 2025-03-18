package com.projecthub.base.shared.security.service;

import org.apache.directory.fortress.core.model.Session;

/**
 * Interface for session management services.
 * This is used to break circular dependencies between modules.
 */
public interface SessionService {
    
    /**
     * Gets the current user session
     * 
     * @return The current user's Session object
     */
    Session getCurrentSession();
    
    /**
     * Checks if the session is valid
     * 
     * @param session The session to validate
     * @return true if the session is valid, false otherwise
     */
    boolean isSessionValid(Session session);
}