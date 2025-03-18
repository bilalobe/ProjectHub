package com.projecthub.compose.features.milestone

import java.time.LocalDate
import java.util.UUID

data class MilestoneState(
    val milestones: List<MilestoneUiModel> = emptyList(),
    val selectedMilestone: MilestoneUiModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class MilestoneUiModel(
    val id: UUID,
    val name: String,
    val description: String,
    val dueDate: LocalDate,
    val progress: Int,
    val status: MilestoneStatus,
    val dependencies: List<UUID> = emptyList()
)

enum class MilestoneStatus {
    PENDING,
    IN_PROGRESS,
    BLOCKED,
    COMPLETED,
    CANCELLED
}