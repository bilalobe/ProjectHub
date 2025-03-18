package com.projecthub.compose.features.milestone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.UUID

class MilestoneViewModel(
    private val projectId: String
) : ViewModel() {
    var state by mutableStateOf(MilestoneState())
        private set

    init {
        loadMilestones()
    }

    private fun loadMilestones() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                // TODO: Integrate with backend API
                // val milestones = milestoneService.getMilestonesForProject(projectId)
                // state = state.copy(milestones = milestones, isLoading = false)
            } catch (e: Exception) {
                state = state.copy(
                    error = "Failed to load milestones: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun selectMilestone(id: UUID) {
        state = state.copy(
            selectedMilestone = state.milestones.find { it.id == id }
        )
    }

    fun updateMilestoneStatus(id: UUID, status: MilestoneStatus) {
        viewModelScope.launch {
            try {
                // TODO: Integrate with backend API
                // milestoneService.updateStatus(id, status)
                loadMilestones()
            } catch (e: Exception) {
                state = state.copy(
                    error = "Failed to update milestone status: ${e.message}"
                )
            }
        }
    }

    fun updateMilestoneProgress(id: UUID, progress: Int) {
        viewModelScope.launch {
            try {
                // TODO: Integrate with backend API
                // milestoneService.updateProgress(id, progress)
                loadMilestones()
            } catch (e: Exception) {
                state = state.copy(
                    error = "Failed to update milestone progress: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        state = state.copy(error = null)
    }
}