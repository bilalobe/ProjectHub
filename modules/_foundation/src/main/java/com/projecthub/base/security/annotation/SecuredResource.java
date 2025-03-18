package com.projecthub.base.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for securing resource access at method level.
 * Used primarily on controller methods to ensure proper authorization.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SecuredResource {

    /**
     * The type of resource being accessed (e.g. "project", "user", "document").
     */
    String resourceType();
    
    /**
     * The action being performed on the resource (e.g. "create", "read", "update", "delete").
     */
    String action();
    
    /**
     * The index of the method parameter that contains the resource ID.
     * For example, in updateProject(UUID projectId, ProjectDTO data),
     * resourceIdParam would be 0 since projectId is the first parameter.
     */
    int resourceIdParam() default 0;
}