package com.projecthub.ui.dashboard.model

import java.time.LocalDateTime

data class DashboardState(
    val metrics: DashboardMetrics = DashboardMetrics(),
    val projectMetrics: ProjectDashboardMetrics = ProjectDashboardMetrics(),
    val recentActivities: List<RecentActivity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class DashboardMetrics(
    val totalUsers: Int = 0,
    val totalProjects: Int = 0,
    val totalTeams: Int = 0,
    val projectStatusDistribution: List<ProjectStatusData> = emptyList()
)

data class ProjectStatusData(
    val status: String,
    val count: Int
)

data class ProjectDashboardMetrics(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val inProgressTasks: Int = 0,
    val pendingTasks: Int = 0,
    val lastUpdated: LocalDateTime = LocalDateTime.now()
)

data class RecentActivity(
    val timestamp: LocalDateTime,
    val activity: String,
    val user: String,
    val type: ActivityType
)

enum class ActivityType {
    PROJECT_CREATED,
    TASK_COMPLETED,
    TEAM_UPDATED,
    SUBMISSION_GRADED,
    USER_JOINED
}

sealed class DashboardEvent {
    object LoadDashboard : DashboardEvent()
    object LoadRecentActivities : DashboardEvent()
    data class LoadProjectMetrics(val projectId: String) : DashboardEvent()
    object ClearError : DashboardEvent()
}