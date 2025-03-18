package com.projecthub.compose.navigation

sealed class Destination(val route: String) {
    object Users : Destination("users")
    object Projects : Destination("projects")
    object Teams : Destination("teams")
    
    // Detail screens
    data class UserDetails(val userId: String) : Destination("users/$userId")
    data class ProjectDetails(val projectId: String) : Destination("projects/$projectId")
    data class TeamDetails(val teamId: String) : Destination("teams/$teamId")
    data class ProjectMilestones(val projectId: String) : Destination("projects/$projectId/milestones")
    data class MilestoneDetails(val projectId: String, val milestoneId: String) : 
        Destination("projects/$projectId/milestones/$milestoneId")
    
    companion object {
        // Route patterns for navigation with parameters
        const val USER_DETAILS_ROUTE = "users/{userId}"
        const val PROJECT_DETAILS_ROUTE = "projects/{projectId}"
        const val TEAM_DETAILS_ROUTE = "teams/{teamId}"
        const val PROJECT_MILESTONES_ROUTE = "projects/{projectId}/milestones"
        const val MILESTONE_DETAILS_ROUTE = "projects/{projectId}/milestones/{milestoneId}"
    }
}