package com.projecthub.base.auth.service.fortress;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Custom method security expression handler for Apache Fortress.
 * This handler adds custom security expressions for Fortress permissions.
 */
@Component
public class FortressMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private final FortressAccessControlService fortressAccessControlService;
    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    public FortressMethodSecurityExpressionHandler(FortressAccessControlService fortressAccessControlService) {
        this.fortressAccessControlService = fortressAccessControlService;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication, MethodInvocation invocation) {
        
        FortressSecurityExpressionRoot root = 
            new FortressSecurityExpressionRoot(authentication, fortressAccessControlService);
        
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        
        return root;
    }
}