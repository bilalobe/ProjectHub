package com.projecthub.compose.features.teams

import com.projecthub.compose.bridge.ServiceBridge
import com.projecthub.compose.mvi.BaseMviViewModel
import kotlinx.coroutines.launch
import com.projecthub.compose.features.teams.TeamsContract.*

class TeamsViewModel : BaseMviViewModel<State, Intent, Effect>(State()) {
    
    override fun handleIntent(intent: Intent, currentState: State) {
        when (intent) {
            is Intent.LoadTeams -> loadTeams()
            is Intent.SelectTeam -> handleTeamSelection(intent.teamId)
            is Intent.FilterTeams -> handleFilter(intent.filter)
            is Intent.SortTeams -> handleSort(intent.order)
            is Intent.RefreshTeams -> loadTeams(forceRefresh = true)
            is Intent.SearchTeams -> handleSearch(intent.query)
            is Intent.UpdateTeamStatus -> updateTeamStatus(intent.teamId, intent.status)
        }
    }
    
    private fun loadTeams(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true, error = null) }
                
                val bridge = ServiceBridge.getInstance()
                bridge.callService<List<Map<String, Any>>>(
                    "teamService",
                    "getTeams",
                    mapOf("forceRefresh" to forceRefresh)
                ).collect { result ->
                    result.onSuccess { teamMaps ->
                        val teams = teamMaps.map { map ->
                            Team(
                                id = map["id"].toString(),
                                name = map["name"].toString(),
                                description = map["description"].toString(),
                                status = TeamStatus.valueOf(map["status"].toString()),
                                memberCount = (map["memberCount"] as Number).toInt(),
                                leadId = map["leadId"]?.toString(),
                                leadName = map["leadName"]?.toString(),
                                activeProjects = (map["activeProjects"] as Number).toInt(),
                                completedProjects = (map["completedProjects"] as Number).toInt(),
                                schoolId = map["schoolId"].toString(),
                                cohortId = map["cohortId"].toString(),
                                lastActive = (map["lastActive"] as Number).toLong()
                            )
                        }
                        updateState { 
                            copy(
                                teams = teams,
                                isLoading = false,
                                error = null
                            )
                        }
                    }.onFailure { error ->
                        val errorMessage = "Failed to load teams: ${error.message}"
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
    
    private fun handleTeamSelection(teamId: String) {
        emitEffect(Effect.NavigateToTeamDetails(teamId))
    }
    
    private fun handleFilter(filter: TeamFilter) {
        viewModelScope.launch {
            updateState { copy(selectedFilter = filter) }
            
            val filteredTeams = state.teams.filter { team ->
                when (filter) {
                    TeamFilter.ALL -> true
                    TeamFilter.ACTIVE -> team.status == TeamStatus.ACTIVE
                    TeamFilter.INACTIVE -> team.status == TeamStatus.INACTIVE
                    TeamFilter.WITH_PROJECTS -> team.activeProjects > 0
                    TeamFilter.NO_PROJECTS -> team.activeProjects == 0
                }
            }
            
            updateState { copy(teams = filteredTeams) }
        }
    }
    
    private fun handleSort(order: TeamSortOrder) {
        viewModelScope.launch {
            updateState { copy(sortOrder = order) }
            
            val sortedTeams = state.teams.sortedWith { a, b ->
                when (order) {
                    TeamSortOrder.NAME_ASC -> a.name.compareTo(b.name)
                    TeamSortOrder.NAME_DESC -> b.name.compareTo(a.name)
                    TeamSortOrder.MEMBER_COUNT_ASC -> a.memberCount.compareTo(b.memberCount)
                    TeamSortOrder.MEMBER_COUNT_DESC -> b.memberCount.compareTo(a.memberCount)
                    TeamSortOrder.ACTIVE_PROJECTS_ASC -> a.activeProjects.compareTo(b.activeProjects)
                    TeamSortOrder.ACTIVE_PROJECTS_DESC -> b.activeProjects.compareTo(a.activeProjects)
                    TeamSortOrder.LAST_ACTIVE_ASC -> a.lastActive.compareTo(b.lastActive)
                    TeamSortOrder.LAST_ACTIVE_DESC -> b.lastActive.compareTo(a.lastActive)
                }
            }
            
            updateState { copy(teams = sortedTeams) }
        }
    }
    
    private fun handleSearch(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadTeams()
                return@launch
            }
            
            try {
                updateState { copy(isLoading = true, error = null) }
                
                val bridge = ServiceBridge.getInstance()
                bridge.callService<List<Map<String, Any>>>(
                    "teamService",
                    "searchTeams",
                    mapOf("query" to query)
                ).collect { result ->
                    result.onSuccess { teamMaps ->
                        val teams = teamMaps.map { map ->
                            Team(
                                id = map["id"].toString(),
                                name = map["name"].toString(),
                                description = map["description"].toString(),
                                status = TeamStatus.valueOf(map["status"].toString()),
                                memberCount = (map["memberCount"] as Number).toInt(),
                                leadId = map["leadId"]?.toString(),
                                leadName = map["leadName"]?.toString(),
                                activeProjects = (map["activeProjects"] as Number).toInt(),
                                completedProjects = (map["completedProjects"] as Number).toInt(),
                                schoolId = map["schoolId"].toString(),
                                cohortId = map["cohortId"].toString(),
                                lastActive = (map["lastActive"] as Number).toLong()
                            )
                        }
                        updateState { 
                            copy(
                                teams = teams,
                                isLoading = false,
                                error = null
                            )
                        }
                    }.onFailure { error ->
                        val errorMessage = "Failed to search teams: ${error.message}"
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
    
    private fun updateTeamStatus(teamId: String, status: TeamStatus) {
        viewModelScope.launch {
            try {
                val bridge = ServiceBridge.getInstance()
                bridge.callService<Unit>(
                    "teamService",
                    "updateTeamStatus",
                    mapOf(
                        "teamId" to teamId,
                        "status" to status.name
                    )
                ).collect { result ->
                    result.onSuccess {
                        // Update local state
                        updateState {
                            copy(
                                teams = teams.map { team ->
                                    if (team.id == teamId) {
                                        team.copy(status = status)
                                    } else {
                                        team
                                    }
                                }
                            )
                        }
                        emitEffect(Effect.ShowSuccess("Team status updated"))
                    }.onFailure { error ->
                        emitEffect(Effect.ShowError("Failed to update team status: ${error.message}"))
                    }
                }
            } catch (e: Exception) {
                emitEffect(Effect.ShowError("Failed to update team status: ${e.message}"))
            }
        }
    }
}