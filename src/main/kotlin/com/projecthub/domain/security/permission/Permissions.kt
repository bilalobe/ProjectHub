package com.projecthub.domain.security.permission

/**
 * Defines all permissions in the system using a sealed class hierarchy.
 * This approach gives type safety and IDE support for permission checking.
 */
sealed class Permission(val value: String) {

    /**
     * Cohort-related permissions
     */
    sealed class Cohort(value: String) : Permission(value) {
        object View : Cohort("cohort:view")
        object Create : Cohort("cohort:create")
        object Update : Cohort("cohort:update")
        object Delete : Cohort("cohort:delete")
        object Archive : Cohort("cohort:archive")

        /**
         * Team-related permissions within cohorts
         */
        sealed class Team(value: String) : Permission(value) {
            object View : Team("cohort:team:view")
            object Manage : Team("cohort:team:manage")
            object Assign : Team("cohort:team:assign")
        }

        /**
         * Seating-related permissions
         */
        sealed class Seating(value: String) : Permission(value) {
            object View : Seating("cohort:seating:view")
            object Configure : Seating("cohort:seating:configure")
            object Manage : Seating("cohort:seating:manage")
            object Assign : Seating("cohort:seating:assign")
            object CreateCustomLayout : Seating("cohort:seating:create-layout")
        }
    }

    /**
     * Project-related permissions
     */
    sealed class Project(value: String) : Permission(value) {
        object View : Project("project:view")
        object Create : Project("project:create")
        object Update : Project("project:update")
        object Delete : Project("project:delete")
        object Archive : Project("project:archive")

        /**
         * Task-related permissions within projects
         */
        sealed class Task(value: String) : Permission(value) {
            object View : Task("project:task:view")
            object Create : Task("project:task:create")
            object Update : Task("project:task:update")
            object Delete : Task("project:task:delete")
            object Assign : Task("project:task:assign")
        }
    }

    /**
     * User-related permissions
     */
    sealed class User(value: String) : Permission(value) {
        object View : User("user:view")
        object Create : User("user:create")
        object Update : User("user:update")
        object Delete : User("user:delete")
        object ManageRoles : User("user:manage-roles")
    }

    /**
     * Admin-specific permissions
     */
    sealed class Admin(value: String) : Permission(value) {
        object AccessDashboard : Admin("admin:dashboard")
        object ConfigureSystem : Admin("admin:configure")
        object ViewLogs : Admin("admin:view-logs")
        object ManageRoles : Admin("admin:manage-roles")
        object ManagePermissions : Admin("admin:manage-permissions")
    }

    /**
     * Data access permissions
     */
    sealed class Data(value: String) : Permission(value) {
        object ViewConfidential : Data("data:view:confidential")
        object ViewRestricted : Data("data:view:restricted")
        object Export : Data("data:export")
    }
}
