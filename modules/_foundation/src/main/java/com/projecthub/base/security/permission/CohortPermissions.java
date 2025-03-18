package com.projecthub.base.security.permission;

/**
 * Defines the permission constants for cohort-related operations.
 * These are used for role-based access control throughout the application.
 */
public class CohortPermissions {
    
    // Base cohort permissions
    public static final String VIEW_COHORTS = "VIEW_COHORTS";
    public static final String MANAGE_COHORTS = "MANAGE_COHORTS";
    public static final String CREATE_COHORT = "CREATE_COHORT";
    public static final String UPDATE_COHORT = "UPDATE_COHORT";
    public static final String DELETE_COHORT = "DELETE_COHORT";
    public static final String ARCHIVE_COHORT = "ARCHIVE_COHORT";
    
    // Team-related permissions within cohorts
    public static final String VIEW_TEAMS = "VIEW_TEAMS";
    public static final String MANAGE_TEAMS = "MANAGE_TEAMS";
    public static final String ASSIGN_TEAM = "ASSIGN_TEAM";
    
    // Seating-specific permissions
    public static final String VIEW_SEATING = "VIEW_SEATING";
    public static final String CONFIGURE_SEATING = "CONFIGURE_SEATING";
    public static final String MANAGE_SEATING = "MANAGE_SEATING";
    public static final String ASSIGN_SEATS = "ASSIGN_SEATS";
    public static final String CREATE_CUSTOM_LAYOUT = "CREATE_CUSTOM_LAYOUT";
    
    // IoT device permissions
    public static final String VIEW_IOT_DEVICES = "VIEW_IOT_DEVICES";
    public static final String MANAGE_IOT_DEVICES = "MANAGE_IOT_DEVICES";
    public static final String CONTROL_IOT_DEVICES = "CONTROL_IOT_DEVICES";
    
    // Reporting permissions
    public static final String VIEW_SEATING_REPORTS = "VIEW_SEATING_REPORTS";
    public static final String EXPORT_SEATING_DATA = "EXPORT_SEATING_DATA";
    
    private CohortPermissions() {
        // Private constructor to prevent instantiation
    }
}