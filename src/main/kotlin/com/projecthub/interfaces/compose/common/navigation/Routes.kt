package com.projecthub.interfaces.ui.navigation

/**
 * Central definition of all navigation routes in the application.
 * This ensures consistent navigation across all UI components.
 */
sealed class Routes(val route: String) {
    // Authentication routes
    object Login : Routes("login")
    object Register : Routes("register")
    
    // User routes
    object UserProfile : Routes("user/profile/{userId}") {
        fun createRoute(userId: String) = "user/profile/$userId"
    }
    object UserList : Routes("user/list")
    
    // Team routes
    object TeamList : Routes("team/list")
    object TeamDetails : Routes("team/details/{teamId}") {
        fun createRoute(teamId: String) = "team/details/$teamId"
    }
    
    // Task routes
    object TaskList : Routes("task/list")
    object TaskDetails : Routes("task/details/{taskId}") {
        fun createRoute(taskId: String) = "task/details/$taskId"
    }
    object TaskDashboard : Routes("task/dashboard")
    
    // Project routes
    object ProjectList : Routes("project/list")
    object ProjectDetails : Routes("project/details/{projectId}") {
        fun createRoute(projectId: String) = "project/details/$projectId"
    }
    
    // Dashboard/Home
    object Dashboard : Routes("dashboard")
    
    companion object {
        fun fromRoute(route: String): Routes? = when {
            route.startsWith("user/profile/") -> UserProfile
            route == UserList.route -> UserList
            route == TeamList.route -> TeamList
            route.startsWith("team/details/") -> TeamDetails
            route == TaskList.route -> TaskList
            route.startsWith("task/details/") -> TaskDetails
            route == TaskDashboard.route -> TaskDashboard
            route == ProjectList.route -> ProjectList
            route.startsWith("project/details/") -> ProjectDetails
            route == Dashboard.route -> Dashboard
            route == Login.route -> Login
            route == Register.route -> Register
            else -> null
        }
    }
}