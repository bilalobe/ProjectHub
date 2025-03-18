package com.projecthub.ui.dashboard.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.projecthub.base.dashboard.application.service.DashboardService
import com.projecthub.base.project.mgmt.domain.service.ProjectService
import com.projecthub.base.task.infrastructure.service.TaskService
import com.projecthub.ui.dashboard.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val dashboardService: DashboardService,
    private val projectService: ProjectService,
    private val taskService: TaskService,
    private val coroutineScope: CoroutineScope
) {
    var state by mutableStateOf(DashboardState())
        private set

    private val _events = MutableSharedFlow<DashboardEvent>()
    val events: SharedFlow<DashboardEvent> = _events.asSharedFlow()

    init {
        coroutineScope.launch {
            _events.collect { event ->
                handleEvent(event)
            }
        }
        loadDashboard()
    }

    private suspend fun handleEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.LoadDashboard -> loadDashboard()
            is DashboardEvent.LoadRecentActivities -> loadRecentActivities()
            is DashboardEvent.LoadProjectMetrics -> loadProjectMetrics(event.projectId)
            is DashboardEvent.ClearError -> clearError()
        }
    }

    private fun loadDashboard() {
        state = state.copy(isLoading = true)
        try {
            val metrics = DashboardMetrics(
                totalUsers = dashboardService.getTotalUsers(),
                totalProjects = dashboardService.getTotalProjects(),
                totalTeams = dashboardService.getTotalTeams(),
                projectStatusDistribution = dashboardService.getProjectStatusDistribution()
                    .map { ProjectStatusData(it.status, it.count) }
            )

            state = state.copy(
                metrics = metrics,
                isLoading = false
            )
            
            // Load recent activities after metrics
            loadRecentActivities()
        } catch (e: Exception) {
            state = state.copy(
                error = e.message ?: "Failed to load dashboard",
                isLoading = false
            )
        }
    }

    private fun loadRecentActivities() {
        try {
            val activities = dashboardService.getRecentActivities()
                .map { dto ->
                    RecentActivity(
                        timestamp = dto.timestamp,
                        activity = dto.activity,
                        user = dto.user,
                        type = when {
                            dto.activity.contains("created project") -> ActivityType.PROJECT_CREATED
                            dto.activity.contains("completed task") -> ActivityType.TASK_COMPLETED
                            dto.activity.contains("updated team") -> ActivityType.TEAM_UPDATED
                            dto.activity.contains("graded submission") -> ActivityType.SUBMISSION_GRADED
                            dto.activity.contains("joined") -> ActivityType.USER_JOINED
                            else -> ActivityType.PROJECT_CREATED
                        }
                    )
                }

            state = state.copy(recentActivities = activities)
        } catch (e: Exception) {
            state = state.copy(
                error = e.message ?: "Failed to load recent activities"
            )
        }
    }

    private fun loadProjectMetrics(projectId: String) {
        try {
            val tasks = taskService.getTasksByProjectId(projectId)
            
            val metrics = ProjectDashboardMetrics(
                totalTasks = tasks.size,
                completedTasks = tasks.count { it.status == "COMPLETED" },
                inProgressTasks = tasks.count { it.status == "IN_PROGRESS" },
                pendingTasks = tasks.count { it.status == "PENDING" }
            )

            state = state.copy(projectMetrics = metrics)
        } catch (e: Exception) {
            state = state.copy(
                error = e.message ?: "Failed to load project metrics"
            )
        }
    }

    private fun clearError() {
        state = state.copy(error = null)
    }

    fun emitEvent(event: DashboardEvent) {
        coroutineScope.launch {
            _events.emit(event)
        }
    }
}