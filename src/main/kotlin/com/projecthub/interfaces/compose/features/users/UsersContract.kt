package com.projecthub.compose.features.users

import com.projecthub.compose.mvi.MviEffect
import com.projecthub.compose.mvi.MviIntent
import com.projecthub.compose.mvi.MviState

/**
 * MVI contract for the Users feature
 */
object UsersContract {
    /**
     * Represents the UI state for the users screen
     */
    data class State(
        val users: List<User> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val selectedFilter: UserFilter = UserFilter.ALL
    ) : MviState

    /**
     * All possible intents/actions that can be performed on the users screen
     */
    sealed interface Intent : MviIntent {
        object LoadUsers : Intent
        data class SelectUser(val userId: String) : Intent
        data class FilterUsers(val filter: UserFilter) : Intent
        object RefreshUsers : Intent
        data class SearchUsers(val query: String) : Intent
    }

    /**
     * One-time side effects that can occur in the users feature
     */
    sealed interface Effect : MviEffect {
        data class NavigateToUserDetails(val userId: String) : Effect
        data class ShowError(val message: String) : Effect
        data class ShowSuccess(val message: String) : Effect
    }
}

/**
 * Represents a user in the system
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val activeAssignments: Int,
    val avatarUrl: String? = null
)

/**
 * Available filters for the users list
 */
enum class UserFilter {
    ALL,
    ACTIVE,
    INACTIVE,
    ADMINS,
    TEAM_LEADS
}