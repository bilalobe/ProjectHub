package com.projecthub.base.auth.api.controller;

import com.projecthub.base.auth.service.fortress.FortressAccessControlService;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller that demonstrates the use of Apache Fortress RBAC.
 * This controller uses custom security expressions from Fortress for authorization.
 */
@RestController
@RequestMapping("/api/v1/rbac-demo")
public class FortressRbacDemoController {

    private static final Logger log = LoggerFactory.getLogger(FortressRbacDemoController.class);
    private final FortressAccessControlService accessControlService;

    public FortressRbacDemoController(FortressAccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    /**
     * Endpoint accessible only to authenticated users.
     * Demonstrates access to user information.
     */
    @GetMapping("/whoami")
    public ResponseEntity<Map<String, Object>> whoAmI() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> userInfo = new HashMap<>();

        userInfo.put("username", auth.getName());
        userInfo.put("authorities", auth.getAuthorities());
        userInfo.put("authenticated", Boolean.valueOf(auth.isAuthenticated()));

        return ResponseEntity.ok(userInfo);
    }

    /**
     * Endpoint that requires the "project:view" permission.
     * Demonstrates the use of custom Fortress security expressions.
     */
    @GetMapping("/projects")
    @PreAuthorize("hasObjectPermission('project', 'view')")
    public ResponseEntity<Map<String, String>> listProjects() {
        log.info("User has 'project:view' permission, accessing projects");

        Map<String, String> result = new HashMap<>();
        result.put("message", "You have access to view projects");
        result.put("status", "success");

        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint that requires the "submission:grade" permission.
     * Only users with instructor or admin roles should have access.
     */
    @GetMapping("/submissions/grade")
    @PreAuthorize("hasObjectPermission('submission', 'grade')")
    public ResponseEntity<Map<String, String>> gradeSubmissions() {
        log.info("User has 'submission:grade' permission, accessing grading");

        Map<String, String> result = new HashMap<>();
        result.put("message", "You have access to grade submissions");
        result.put("status", "success");

        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint that checks a specific role using Fortress.
     * Demonstrates role-based access control.
     */
    @GetMapping("/roles/{roleName}")
    public ResponseEntity<Map<String, Object>> checkRole(@PathVariable String roleName) {
        boolean hasRole = FortressAccessControlService.hasRole(roleName);

        @NonNls @NonNls @NonNls @NonNls Map<String, Object> result = new HashMap<>();
        result.put("role", roleName);
        result.put("hasRole", Boolean.valueOf(hasRole));

        if (hasRole) {
            result.put("message", "You have the " + roleName + " role");
        } else {
            result.put("message", "You do not have the " + roleName + " role");
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Admin-only endpoint that requires the ADMIN role.
     * Demonstrates role-based authorization.
     */
    @GetMapping("/admin-area")
    @PreAuthorize("hasAnyFortressRole('ADMIN')")
    public ResponseEntity<Map<String, String>> adminOnlyArea() {
        log.info("User has ADMIN role, accessing admin area");

        Map<String, String> result = new HashMap<>();
        result.put("message", "Welcome to the admin area");
        result.put("status", "success");

        return ResponseEntity.ok(result);
    }
}
