package com.projecthub.ui.report

import com.projecthub.base.dashboard.api.dto.ActivityDTO
import com.projecthub.base.dashboard.api.dto.ProjectStatusDTO

data class ReportState(
    val totalUsers: Int = 0,
    val totalProjects: Int = 0,
    val totalTeams: Int = 0,
    val projectStatusDistribution: List<ProjectStatusDTO> = emptyList(),
    val recentActivities: List<ActivityDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedDateRange: Pair<String, String>? = null
)

sealed interface ReportIntent {
    object LoadDashboard : ReportIntent
    object RefreshData : ReportIntent
    data class FilterByDateRange(val range: Pair<String, String>) : ReportIntent
    object ExportReport : ReportIntent
}

sealed interface ReportEffect {
    data class ShowError(val message: String) : ReportEffect
    data class ExportComplete(val filePath: String) : ReportEffect
    object ShowSuccessMessage : ReportEffect
}