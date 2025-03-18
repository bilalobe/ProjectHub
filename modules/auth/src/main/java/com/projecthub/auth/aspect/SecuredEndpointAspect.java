package com.projecthub.auth.aspect;

import com.projecthub.auth.annotation.SecuredEndpoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
public class SecuredEndpointAspect {

    @Around("@annotation(com.projecthub.auth.annotation.SecuredEndpoint) || @within(com.projecthub.auth.annotation.SecuredEndpoint)")
    public Object enforceSecurityCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = joinPoint.getTarget().getClass();

        SecuredEndpoint methodAnnotation = AnnotationUtils.findAnnotation(method, SecuredEndpoint.class);
        SecuredEndpoint classAnnotation = AnnotationUtils.findAnnotation(targetClass, SecuredEndpoint.class);

        Set<String> requiredRoles = getRequiredRoles(methodAnnotation, classAnnotation);
        Set<String> requiredPermissions = getRequiredPermissions(methodAnnotation, classAnnotation);

        if (!hasRequiredAuthorities(authentication, requiredRoles, requiredPermissions)) {
            throw new AccessDeniedException("Access denied");
        }

        return joinPoint.proceed();
    }

    private Set<String> getRequiredRoles(SecuredEndpoint methodAnnotation, SecuredEndpoint classAnnotation) {
        Set<String> roles = Collections.emptySet();
        if (methodAnnotation != null && methodAnnotation.roles().length > 0) {
            roles = Arrays.stream(methodAnnotation.roles())
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .collect(Collectors.toSet());
        } else if (classAnnotation != null && classAnnotation.roles().length > 0) {
            roles = Arrays.stream(classAnnotation.roles())
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .collect(Collectors.toSet());
        }
        return roles;
    }

    private Set<String> getRequiredPermissions(SecuredEndpoint methodAnnotation, SecuredEndpoint classAnnotation) {
        if (methodAnnotation != null && methodAnnotation.permissions().length > 0) {
            return Arrays.stream(methodAnnotation.permissions()).collect(Collectors.toSet());
        } else if (classAnnotation != null && classAnnotation.permissions().length > 0) {
            return Arrays.stream(classAnnotation.permissions()).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    private boolean hasRequiredAuthorities(Authentication authentication, 
                                        Set<String> requiredRoles,
                                        Set<String> requiredPermissions) {
        if (requiredRoles.isEmpty() && requiredPermissions.isEmpty()) {
            return true;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Set<String> userAuthorities = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        boolean hasRequiredRoles = requiredRoles.isEmpty() ||
                requiredRoles.stream().anyMatch(userAuthorities::contains);
        boolean hasRequiredPermissions = requiredPermissions.isEmpty() ||
                requiredPermissions.stream().anyMatch(userAuthorities::contains);

        return hasRequiredRoles && hasRequiredPermissions;
    }
}