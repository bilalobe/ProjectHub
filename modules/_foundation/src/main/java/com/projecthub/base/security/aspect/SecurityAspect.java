package com.projecthub.base.security.aspect;

import com.projecthub.base.security.annotation.SecuredResource;
import com.projecthub.base.security.exception.AccessDeniedException;
import com.projecthub.base.security.facade.SecurityFacade;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * Security aspect that enforces authorization rules on endpoints.
 * Uses AOP to apply security checks based on annotations.
 */
@Aspect
@Component
@Order(1)
public class SecurityAspect {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAspect.class);
    private final SecurityFacade securityFacade;

    public SecurityAspect(SecurityFacade securityFacade) {
        this.securityFacade = securityFacade;
    }

    /**
     * Applies security checks to methods annotated with @SecuredResource.
     * Extracts the resource ID from the method parameters based on the annotation configuration.
     *
     * @param joinPoint The point in the code execution
     * @return The result of the method execution if authorization passes
     * @throws Throwable if access is denied or an error occurs
     */
    @Around("@annotation(com.projecthub.base.security.annotation.SecuredResource)")
    public Object enforceResourceSecurity(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get user ID from the request headers (Set by gateway)
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String userId = request.getHeader("X-User-ID");
        
        if (userId == null || userId.trim().isEmpty()) {
            logger.warn("Resource access attempted without a user ID");
            throw new AccessDeniedException("Authentication required");
        }
        
        // Get annotation metadata
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        SecuredResource securedResource = method.getAnnotation(SecuredResource.class);
        
        // Extract resource type and action
        String resourceType = securedResource.resourceType();
        String action = securedResource.action();
        
        // Extract resource ID based on parameter index specified in annotation
        Object[] args = joinPoint.getArgs();
        int resourceIdIndex = securedResource.resourceIdParam();
        
        if (resourceIdIndex >= 0 && resourceIdIndex < args.length) {
            Object resourceId = args[resourceIdIndex];
            
            // Check permission based on resource type
            boolean hasPermission = checkPermission(userId, resourceType, resourceId.toString(), action);
            
            if (!hasPermission) {
                logger.warn("Access denied for user {} to {} {} {}", userId, action, resourceType, resourceId);
                throw new AccessDeniedException("Insufficient permissions");
            }
            
            logger.debug("Access granted for user {} to {} {} {}", userId, action, resourceType, resourceId);
        } else {
            logger.warn("Invalid resourceIdParam index in @SecuredResource annotation");
        }
        
        return joinPoint.proceed();
    }
    
    /**
     * Checks permissions based on resource type.
     *
     * @param userId The ID of the user
     * @param resourceType The type of resource being accessed
     * @param resourceId The ID of the resource
     * @param action The action being performed
     * @return true if access is permitted, false otherwise
     */
    private boolean checkPermission(String userId, String resourceType, String resourceId, String action) {
        switch (resourceType) {
            case "project":
                return securityFacade.hasProjectPermission(userId, resourceId, action);
            case "data":
                return securityFacade.canAccessSensitiveData(userId, resourceId);
            default:
                // For other resources, check general permission format
                return securityFacade.getUserPermissions(userId).contains(resourceType + ":" + action);
        }
    }
}