package com.projecthub.compose.features.teams

import com.projecthub.compose.mvi.MviEffect
import com.projecthub.compose.mvi.MviIntent
import com.projecthub.compose.mvi.MviState

object TeamsContract {
    data class State(
        val teams: List<Team> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val selectedFilter: TeamFilter = TeamFilter.ALL,
        val sortOrder: TeamSortOrder = TeamSortOrder.NAME_ASC
    ) : MviState

    sealed interface Intent : MviIntent {
        object LoadTeams : Intent
        data class SelectTeam(val teamId: String) : Intent
        data class FilterTeams(val filter: TeamFilter) : Intent
        data class SortTeams(val order: TeamSortOrder) : Intent
        object RefreshTeams : Intent
        data class SearchTeams(val query: String) : Intent
        data class UpdateTeamStatus(val teamId: String, val status: TeamStatus) : Intent
    }

    sealed interface Effect : MviEffect {
        data class NavigateToTeamDetails(val teamId: String) : Effect
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
    }
}

data class Team(
    val id: String,
    val name: String,
    val description: String,
    val status: TeamStatus,
    val memberCount: Int,
    val leadId: String?,
    val leadName: String?,
    val activeProjects: Int,
    val completedProjects: Int,
    val schoolId: String,
    val cohortId: String,
    val lastActive: Long
)

enum class TeamStatus {
    ACTIVE,
    INACTIVE,
    ARCHIVED
}

enum class TeamFilter {
    ALL,
    ACTIVE,
    INACTIVE,
    WITH_PROJECTS,
    NO_PROJECTS
}

enum class TeamSortOrder {
    NAME_ASC,
    NAME_DESC,
    MEMBER_COUNT_ASC,
    MEMBER_COUNT_DESC,
    ACTIVE_PROJECTS_ASC,
    ACTIVE_PROJECTS_DESC,
    LAST_ACTIVE_ASC,
    LAST_ACTIVE_DESC
}