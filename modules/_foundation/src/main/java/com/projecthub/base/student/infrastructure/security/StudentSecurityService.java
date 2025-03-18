package com.projecthub.base.student.infrastructure.security;

import com.projecthub.base.auth.service.fortress.FortressSessionService;
import com.projecthub.base.shared.config.RbacSecurityConfig;
import com.projecthub.base.student.infrastructure.security.StudentSecurityConfig.ObjectIdentifiers;
import com.projecthub.base.student.infrastructure.security.StudentSecurityConfig.Operations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.model.Session;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Security service for the Student module.
 * Enforces RBAC permissions at the service layer using Apache Fortress.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentSecurityService {

    private final AccessMgr accessManager;
    private final FortressSessionService sessionService;
    @Qualifier("")
    private final RbacSecurityConfig securityConfig;

    /**
     * Enforce "create" permission check for students
     */
    public void enforceCreatePermission() {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.STUDENT, Operations.CREATE)) {
            throw new AccessDeniedException("User does not have permission to create student records");
        }
    }

    /**
     * Enforce "read" permission check for a specific student
     *
     * @param studentId The student identifier
     */
    public void enforceReadPermission(UUID studentId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.STUDENT, Operations.READ)) {
            throw new AccessDeniedException("User does not have permission to read student details");
        }
        // Further specific rules could be implemented here, e.g., checks if the user is the student's advisor
    }

    /**
     * Enforce "update" permission check for a specific student
     *
     * @param studentId The student identifier
     */
    public void enforceUpdatePermission(UUID studentId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.STUDENT, Operations.UPDATE)) {
            throw new AccessDeniedException("User does not have permission to update student details");
        }
    }

    /**
     * Enforce "delete" permission check for a specific student
     *
     * @param studentId The student identifier
     */
    public void enforceDeletePermission(UUID studentId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.STUDENT, Operations.DELETE)) {
            throw new AccessDeniedException("User does not have permission to delete student records");
        }
    }

    /**
     * Enforce "enroll" permission check for a student
     *
     * @param studentId The student identifier
     */
    public void enforceEnrollPermission(UUID studentId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.STUDENT, Operations.ENROLL)) {
            throw new AccessDeniedException("User does not have permission to enroll students");
        }
    }

    /**
     * Enforce "view records" permission check for a student's academic records
     *
     * @param studentId The student identifier
     */
    public void enforceViewAcademicRecordsPermission(UUID studentId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.STUDENT_ACADEMIC, Operations.VIEW_RECORDS)) {
            throw new AccessDeniedException("User does not have permission to view student academic records");
        }
    }

    /**
     * Enforce "grade" permission check for a student's assignments
     *
     * @param studentId The student identifier
     */
    public void enforceGradePermission(UUID studentId) {
        Session session = getCurrentSession();
        if (!securityConfig.hasPermission(session, ObjectIdentifiers.STUDENT_ACADEMIC, Operations.GRADE)) {
            throw new AccessDeniedException("User does not have permission to grade student assignments");
        }
    }

    /**
     * Get the current user's Fortress session from the security context
     *
     * @return The current Fortress session
     * @throws AccessDeniedException if no authenticated user is found
     */
    private Session getCurrentSession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AccessDeniedException("No authenticated user found");
        }
        return sessionService.getSession(authentication);
    }
}
