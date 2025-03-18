package com.projecthub.compose.features.users

import com.projecthub.compose.bridge.ServiceBridge
import com.projecthub.compose.mvi.BaseMviViewModel
import kotlinx.coroutines.launch
import com.projecthub.compose.features.users.UsersContract.*

class UsersViewModel : BaseMviViewModel<State, Intent, Effect>(State()) {
    
    override fun handleIntent(intent: Intent, currentState: State) {
        when (intent) {
            is Intent.LoadUsers -> loadUsers()
            is Intent.SelectUser -> handleUserSelection(intent.userId)
            is Intent.FilterUsers -> handleFilter(intent.filter)
            is Intent.RefreshUsers -> loadUsers(forceRefresh = true)
            is Intent.SearchUsers -> handleSearch(intent.query)
        }
    }
    
    private fun loadUsers(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true, error = null) }
                
                val bridge = ServiceBridge.getInstance()
                bridge.callService<List<Map<String, Any>>>(
                    "userService",
                    "getUsers",
                    mapOf("forceRefresh" to forceRefresh)
                ).collect { result ->
                    result.onSuccess { userMaps ->
                        val users = userMaps.map { map ->
                            User(
                                id = map["id"].toString(),
                                name = map["name"].toString(),
                                email = map["email"].toString(),
                                role = map["role"].toString(),
                                activeAssignments = (map["activeAssignments"] as? Number)?.toInt() ?: 0,
                                avatarUrl = map["avatarUrl"] as? String
                            )
                        }
                        updateState { 
                            copy(
                                users = users,
                                isLoading = false,
                                error = null
                            )
                        }
                    }.onFailure { error ->
                        val errorMessage = "Failed to load users: ${error.message}"
                        updateState { 
                            copy(
                                isLoading = false,
                                error = errorMessage
                            )
                        }
                        emitEffect(Effect.ShowError(errorMessage))
                    }
                }
            } catch (e: Exception) {
                val errorMessage = "An error occurred: ${e.message}"
                updateState { 
                    copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
                emitEffect(Effect.ShowError(errorMessage))
            }
        }
    }
    
    private fun handleUserSelection(userId: String) {
        emitEffect(Effect.NavigateToUserDetails(userId))
    }
    
    private fun handleFilter(filter: UserFilter) {
        viewModelScope.launch {
            updateState { copy(selectedFilter = filter) }
            
            // Apply filter to current users list
            val filteredUsers = state.users.filter { user ->
                when (filter) {
                    UserFilter.ALL -> true
                    UserFilter.ACTIVE -> user.activeAssignments > 0
                    UserFilter.INACTIVE -> user.activeAssignments == 0
                    UserFilter.ADMINS -> user.role.equals("Admin", ignoreCase = true)
                    UserFilter.TEAM_LEADS -> user.role.equals("Team Lead", ignoreCase = true)
                }
            }
            
            updateState { copy(users = filteredUsers) }
        }
    }
    
    private fun handleSearch(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadUsers()
                return@launch
            }
            
            try {
                updateState { copy(isLoading = true, error = null) }
                
                val bridge = ServiceBridge.getInstance()
                bridge.callService<List<Map<String, Any>>>(
                    "userService",
                    "searchUsers",
                    mapOf("query" to query)
                ).collect { result ->
                    result.onSuccess { userMaps ->
                        val users = userMaps.map { map ->
                            User(
                                id = map["id"].toString(),
                                name = map["name"].toString(),
                                email = map["email"].toString(),
                                role = map["role"].toString(),
                                activeAssignments = (map["activeAssignments"] as? Number)?.toInt() ?: 0,
                                avatarUrl = map["avatarUrl"] as? String
                            )
                        }
                        updateState { 
                            copy(
                                users = users,
                                isLoading = false,
                                error = null
                            )
                        }
                    }.onFailure { error ->
                        val errorMessage = "Failed to search users: ${error.message}"
                        updateState { 
                            copy(
                                isLoading = false,
                                error = errorMessage
                            )
                        }
                        emitEffect(Effect.ShowError(errorMessage))
                    }
                }
            } catch (e: Exception) {
                val errorMessage = "An error occurred while searching: ${e.message}"
                updateState { 
                    copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
                emitEffect(Effect.ShowError(errorMessage))
            }
        }
    }
}