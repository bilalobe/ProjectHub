package com.projecthub.base.shared.security.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.directory.fortress.core.model.Session;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Default implementation of SessionService that can be used during development
 * or when the auth module is not available.
 * This implementation provides basic session handling functionality.
 */
@Service
@Slf4j
@Profile("!auth")
public class DefaultSessionService implements SessionService {
    
    private ThreadLocal<Session> currentSession = new ThreadLocal<>();
    
    @Override
    public Session getCurrentSession() {
        Session session = currentSession.get();
        if (session == null) {
            log.warn("No session found in current thread. Using default session.");
            session = createDefaultSession();
            currentSession.set(session);
        }
        return session;
    }
    
    @Override
    public boolean isSessionValid(Session session) {
        // Basic validation checks
        return session != null && 
               session.getSessionId() != null && 
               !session.getSessionId().isEmpty();
    }
    
    /**
     * Set the current session for the thread
     * 
     * @param session The session to set as current
     */
    public void setCurrentSession(Session session) {
        currentSession.set(session);
    }
    
    /**
     * Create a default session for development purposes
     * 
     * @return A basic default session
     */
    private Session createDefaultSession() {
        Session session = new Session();
        session.setSessionId("default-session-" + System.currentTimeMillis());
        session.setAuthenticated(true);
        session.setUserId("default-user");
        return session;
    }
    
    /**
     * Clear the current session
     */
    public void clearCurrentSession() {
        currentSession.remove();
    }
}