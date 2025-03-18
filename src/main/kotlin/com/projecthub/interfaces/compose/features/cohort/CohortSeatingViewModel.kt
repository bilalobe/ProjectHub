package com.projecthub.ui.cohort

import androidx.compose.runtime.Stable
import com.projecthub.base.cohort.domain.value.SeatingConfiguration
import com.projecthub.ui.cohort.repository.CohortRepository
import com.projecthub.ui.cohort.repository.TeamRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

@Stable
class CohortSeatingViewModel(
    private val cohortRepository: CohortRepository,
    private val teamRepository: TeamRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    // UI state for the cohort seating screen
    private val _uiState = MutableStateFlow(CohortSeatingUiState())
    val uiState: StateFlow<CohortSeatingUiState> = _uiState.asStateFlow()

    // State for the seating configuration
    private val _seatingConfig = MutableStateFlow<SeatingConfiguration?>(null)
    val seatingConfig: StateFlow<SeatingConfiguration?> = _seatingConfig.asStateFlow()

    // State for team positions and names
    private val _teamPositions = MutableStateFlow<Map<Pair<Int, Int>, UUID>>(emptyMap())
    val teamPositions: StateFlow<Map<Pair<Int, Int>, UUID>> = _teamPositions.asStateFlow()

    private val _teamNames = MutableStateFlow<Map<UUID, String>>(emptyMap())
    val teamNames: StateFlow<Map<UUID, String>> = _teamNames.asStateFlow()

    /**
     * Load cohort data including teams and seating configuration.
     */
    fun loadCohortData(cohortId: UUID) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val cohort = withContext(dispatcher) {
                    cohortRepository.getCohortById(cohortId)
                }

                // Load teams
                val teams = withContext(dispatcher) {
                    teamRepository.getTeamsByCohortId(cohortId)
                }

                // Load seating configuration
                val config = withContext(dispatcher) {
                    cohortRepository.getSeatingConfiguration(cohortId)
                }

                // Build team positions map
                val positions = withContext(dispatcher) {
                    teamRepository.getTeamPositions(cohortId)
                }

                // Map team positions from repository format to UI format
                val positionMap = positions.entries.associate { (teamId, position) ->
                    Pair(position.first, position.second) to teamId
                }

                // Map team names for easy display
                val namesMap = teams.associate { it.id to it.name }

                // Update all state flows
                _seatingConfig.value = config
                _teamPositions.value = positionMap
                _teamNames.value = namesMap

                _uiState.update {
                    it.copy(
                        cohortId = cohortId,
                        cohortName = cohort.name,
                        teams = teams.map { team -> 
                            Team(team.id, team.name, team.position)
                        },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to load cohort data: ${e.message}")
                }
            }
        }
    }

    /**
     * Configure the seating arrangement for the cohort.
     */
    fun configureSeating(rows: Int, columns: Int, layoutType: String, assignmentStrategy: String) {
        val cohortId = _uiState.value.cohortId ?: return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val updatedCohort = withContext(dispatcher) {
                    cohortRepository.configureSeating(cohortId, rows, columns, layoutType, assignmentStrategy)
                }

                // Clear any existing team positions if the grid dimensions changed
                val oldConfig = _seatingConfig.value
                if (oldConfig != null && (oldConfig.rows != rows || oldConfig.columns != columns)) {
                    _teamPositions.value = emptyMap()
                }

                _seatingConfig.value = updatedCohort.seatingConfig

                // If a strategy other than MANUAL was selected, apply it immediately
                if (assignmentStrategy != "MANUAL") {
                    applySeatingStrategy(assignmentStrategy)
                }

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to configure seating: ${e.message}")
                }
            }
        }
    }

    /**
     * Assign a team to a position in the seating arrangement.
     */
    fun assignTeamToPosition(teamId: UUID, row: Int, column: Int) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                // Check if position is available
                val cohortId = _uiState.value.cohortId ?: return@launch
                val isAvailable = withContext(dispatcher) {
                    cohortRepository.isPositionAvailable(cohortId, row, column)
                }

                if (!isAvailable) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Position ($row, $column) is already occupied"
                        )
                    }
                    return@launch
                }

                // Update team position
                val updatedTeam = withContext(dispatcher) {
                    teamRepository.updateTeamPosition(teamId, row, column)
                }

                // Update the positions map
                val updatedPositions = _teamPositions.value.toMutableMap()
                
                // Remove old position if it exists
                updatedPositions.entries.find { it.value == teamId }?.let { 
                    updatedPositions.remove(it.key)
                }
                
                // Add new position
                updatedPositions[Pair(row, column)] = teamId
                _teamPositions.value = updatedPositions

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to assign team position: ${e.message}")
                }
            }
        }
    }

    /**
     * Clear a team's position.
     */
    fun clearTeamPosition(teamId: UUID) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                // Clear the team's position
                val updatedTeam = withContext(dispatcher) {
                    teamRepository.clearTeamPosition(teamId)
                }

                // Update the positions map
                val updatedPositions = _teamPositions.value.toMutableMap()
                updatedPositions.entries.find { it.value == teamId }?.let { 
                    updatedPositions.remove(it.key)
                }
                _teamPositions.value = updatedPositions

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to clear team position: ${e.message}")
                }
            }
        }
    }

    /**
     * Apply a seating strategy to automatically assign positions.
     */
    fun applySeatingStrategy(strategy: String) {
        val cohortId = _uiState.value.cohortId ?: return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                // Apply the selected strategy
                withContext(dispatcher) {
                    cohortRepository.applySeatingStrategy(cohortId, strategy)
                }

                // Refresh team positions
                val positions = withContext(dispatcher) {
                    teamRepository.getTeamPositions(cohortId)
                }

                // Map team positions from repository format to UI format
                val positionMap = positions.entries.associate { (teamId, position) ->
                    Pair(position.first, position.second) to teamId
                }
                _teamPositions.value = positionMap

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to apply seating strategy: ${e.message}")
                }
            }
        }
    }
}

/**
 * UI state for the cohort seating screen.
 */
data class CohortSeatingUiState(
    val cohortId: UUID? = null,
    val cohortName: String = "",
    val teams: List<Team> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Simplified team data structure for UI.
 */
data class Team(
    val id: UUID,
    val name: String,
    val position: com.projecthub.base.team.domain.value.TeamPosition? = null
)