package com.projecthub.compose.features.projects

import com.projecthub.compose.mvi.MviEffect
import com.projecthub.compose.mvi.MviIntent
import com.projecthub.compose.mvi.MviState
import java.util.Date

object ProjectsContract {
    data class State(
        val projects: List<Project> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val selectedFilter: ProjectFilter = ProjectFilter.ALL,
        val sortOrder: ProjectSortOrder = ProjectSortOrder.NEWEST_FIRST
    ) : MviState

    sealed interface Intent : MviIntent {
        object LoadProjects : Intent
        data class SelectProject(val projectId: String) : Intent
        data class FilterProjects(val filter: ProjectFilter) : Intent
        data class SortProjects(val order: ProjectSortOrder) : Intent
        object RefreshProjects : Intent
        data class SearchProjects(val query: String) : Intent
        data class UpdateProjectStatus(val projectId: String, val status: ProjectStatus) : Intent
    }

    sealed interface Effect : MviEffect {
        data class NavigateToProjectDetails(val projectId: String) : Effect
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
    }
}

data class Project(
    val id: String,
    val name: String,
    val description: String,
    val status: ProjectStatus,
    val progress: Float,
    val startDate: Date,
    val dueDate: Date?,
    val teamSize: Int,
    val leadId: String?,
    val leadName: String?,
    val tags: List<String> = emptyList()
)

enum class ProjectStatus {
    PLANNING,
    IN_PROGRESS,
    ON_HOLD,
    COMPLETED,
    CANCELLED
}

enum class ProjectFilter {
    ALL,
    ACTIVE,
    COMPLETED,
    OVERDUE,
    MY_PROJECTS
}

enum class ProjectSortOrder {
    NEWEST_FIRST,
    OLDEST_FIRST,
    NAME_ASC,
    NAME_DESC,
    PROGRESS_ASC,
    PROGRESS_DESC,
    DUE_DATE_ASC,
    DUE_DATE_DESC
}