package com.projecthub.ui.report

import com.projecthub.base.dashboard.application.service.DashboardService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReportViewModel(
    private val dashboardService: DashboardService
) {
    private val _state = MutableStateFlow(ReportState())
    val state: StateFlow<ReportState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<ReportEffect>()
    val effects = _effects.asSharedFlow()

    init {
        handleIntent(ReportIntent.LoadDashboard)
    }

    fun handleIntent(intent: ReportIntent) {
        when (intent) {
            is ReportIntent.LoadDashboard -> loadDashboardData()
            is ReportIntent.RefreshData -> loadDashboardData()
            is ReportIntent.FilterByDateRange -> filterByDateRange(intent.range)
            is ReportIntent.ExportReport -> exportReport()
        }
    }

    private fun loadDashboardData() {
        _state.value = _state.value.copy(isLoading = true)
        try {
            _state.value = _state.value.copy(
                totalUsers = dashboardService.getTotalUsers(),
                totalProjects = dashboardService.getTotalProjects(),
                totalTeams = dashboardService.getTotalTeams(),
                projectStatusDistribution = dashboardService.getProjectStatusDistribution(),
                recentActivities = dashboardService.getRecentActivities(),
                isLoading = false
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                error = e.message,
                isLoading = false
            )
        }
    }

    private fun filterByDateRange(range: Pair<String, String>) {
        _state.value = _state.value.copy(
            selectedDateRange = range,
            isLoading = true
        )
        try {
            val (start, end) = range
            _state.value = _state.value.copy(
                recentActivities = dashboardService.getActivitiesBetween(
                    LocalDateTime.parse(start),
                    LocalDateTime.parse(end)
                ),
                isLoading = false
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                error = e.message,
                isLoading = false
            )
        }
    }

    private fun exportReport() {
        try {
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val filePath = dashboardService.generateReport(timestamp)
            _effects.tryEmit(ReportEffect.ExportComplete(filePath))
        } catch (e: Exception) {
            _effects.tryEmit(ReportEffect.ShowError(e.message ?: "Error exporting report"))
        }
    }
}