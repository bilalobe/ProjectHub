package com.projecthub.ui.admin.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.projecthub.base.admin.AdminService
import com.projecthub.base.audit.AuditService
import com.projecthub.base.user.UserService
import com.projecthub.ui.admin.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class AdminViewModel(
    private val adminService: AdminService,
    private val userService: UserService,
    private val auditService: AuditService,
    private val coroutineScope: CoroutineScope
) {
    var state by mutableStateOf(AdminState())
        private set

    private val _events = MutableSharedFlow<AdminEvent>()
    val events: SharedFlow<AdminEvent> = _events.asSharedFlow()

    init {
        coroutineScope.launch {
            _events.collect { event ->
                handleEvent(event)
            }
        }
    }

    private suspend fun handleEvent(event: AdminEvent) {
        when (event) {
            is AdminEvent.LoadSettings -> loadSettings()
            is AdminEvent.LoadUsers -> loadUsers()
            is AdminEvent.LoadAuditLogs -> loadAuditLogs()
            is AdminEvent.LoadSystemHealth -> loadSystemHealth()
            is AdminEvent.UpdateSettings -> updateSettings(event.settings)
            is AdminEvent.UpdateUserRole -> updateUserRole(event.userId, event.roles)
            is AdminEvent.UpdateUserStatus -> updateUserStatus(event.userId, event.status)
            is AdminEvent.SelectTab -> selectTab(event.tab)
            is AdminEvent.FilterAuditLogs -> filterAuditLogs(event.startDate, event.endDate, event.category)
            is AdminEvent.ClearError -> clearError()
        }
    }

    private fun loadSettings() {
        state = state.copy(isLoading = true)
        try {
            val settings = adminService.getSystemSettings().let { dto ->
                SystemSettings(
                    userRegistrationEnabled = dto.userRegistrationEnabled,
                    maxTeamSize = dto.maxTeamSize,
                    maxProjectsPerUser = dto.maxProjectsPerUser,
                    maxFileUploadSize = dto.maxFileUploadSize,
                    allowedFileTypes = dto.allowedFileTypes.toSet(),
                    emailNotificationsEnabled = dto.emailNotificationsEnabled,
                    sessionTimeoutMinutes = dto.sessionTimeoutMinutes,
                    maintenanceMode = dto.maintenanceMode,
                    maintenanceMessage = dto.maintenanceMessage
                )
            }
            state = state.copy(settings = settings, isLoading = false)
        } catch (e: Exception) {
            state = state.copy(
                error = e.message ?: "Failed to load settings",
                isLoading = false
            )
        }
    }

    private fun loadUsers() {
        state = state.copy(isLoading = true)
        try {
            val users = userService.getAllUsers().map { dto ->
                AdminUserInfo(
                    id = dto.id.toString(),
                    username = dto.username,
                    email = dto.email,
                    roles = dto.roles.map { UserRole.valueOf(it) }.toSet(),
                    status = UserStatus.valueOf(dto.status),
                    lastLogin = dto.lastLogin,
                    createdAt = dto.createdAt
                )
            }
            state = state.copy(users = users, isLoading = false)
        } catch (e: Exception) {
            state = state.copy(
                error = e.message ?: "Failed to load users",
                isLoading = false
            )
        }
    }

    private fun loadAuditLogs() {
        state = state.copy(isLoading = true)
        try {
            val logs = auditService.getAuditLogs().map { dto ->
                AuditLogEntry(
                    id = dto.id.toString(),
                    timestamp = dto.timestamp,
                    user = dto.user,
                    action = dto.action,
                    category = AuditCategory.valueOf(dto.category),
                    details = dto.details,
                    ipAddress = dto.ipAddress
                )
            }
            state = state.copy(auditLogs = logs, isLoading = false)
        } catch (e: Exception) {
            state = state.copy(
                error = e.message ?: "Failed to load audit logs",
                isLoading = false
            )
        }
    }

    private fun loadSystemHealth() {
        state = state.copy(isLoading = true)
        try {
            val health = adminService.getSystemHealth().let { dto ->
                SystemHealth(
                    status = SystemStatus.valueOf(dto.status),
                    metrics = SystemMetrics(
                        cpuUsage = dto.metrics.cpuUsage,
                        memoryUsage = dto.metrics.memoryUsage,
                        diskUsage = dto.metrics.diskUsage,
                        activeUsers = dto.metrics.activeUsers,
                        requestsPerMinute = dto.metrics.requestsPerMinute,
                        averageResponseTime = dto.metrics.averageResponseTime
                    ),
                    services = dto.services.map { service ->
                        ServiceStatus(
                            name = service.name,
                            status = SystemStatus.valueOf(service.status),
                            uptime = service.uptime,
                            lastError = service.lastError
                        )
                    }
                )
            }
            state = state.copy(systemHealth = health, isLoading = false)
        } catch (e: Exception) {
            state = state.copy(
                error = e.message ?: "Failed to load system health",
                isLoading = false
            )
        }
    }

    private fun updateSettings(settings: SystemSettings) {
        state = state.copy(isLoading = true)
        try {
            adminService.updateSystemSettings(settings)
            state = state.copy(settings = settings, isLoading = false)
        } catch (e: Exception) {
            state = state.copy(
                error = e.message ?: "Failed to update settings",
                isLoading = false
            )
        }
    }

    private fun updateUserRole(userId: String, roles: Set<UserRole>) {
        state = state.copy(isLoading = true)
        try {
            userService.updateUserRoles(userId, roles.map { it.name })
            loadUsers() // Reload users to reflect changes
        } catch (e: Exception) {
            state = state.copy(
                error = e.message ?: "Failed to update user roles",
                isLoading = false
            )
        }
    }

    private fun updateUserStatus(userId: String, status: UserStatus) {
        state = state.copy(isLoading = true)
        try {
            userService.updateUserStatus(userId, status.name)
            loadUsers() // Reload users to reflect changes
        } catch (e: Exception) {
            state = state.copy(
                error = e.message ?: "Failed to update user status",
                isLoading = false
            )
        }
    }

    private fun selectTab(tab: AdminTab) {
        state = state.copy(selectedTab = tab)
        when (tab) {
            AdminTab.SETTINGS -> loadSettings()
            AdminTab.USERS -> loadUsers()
            AdminTab.AUDIT -> loadAuditLogs()
            AdminTab.HEALTH -> loadSystemHealth()
        }
    }

    private fun filterAuditLogs(
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        category: AuditCategory?
    ) {
        state = state.copy(isLoading = true)
        try {
            val logs = auditService.getFilteredAuditLogs(
                startDate,
                endDate,
                category?.name
            ).map { dto ->
                AuditLogEntry(
                    id = dto.id.toString(),
                    timestamp = dto.timestamp,
                    user = dto.user,
                    action = dto.action,
                    category = AuditCategory.valueOf(dto.category),
                    details = dto.details,
                    ipAddress = dto.ipAddress
                )
            }
            state = state.copy(auditLogs = logs, isLoading = false)
        } catch (e: Exception) {
            state = state.copy(
                error = e.message ?: "Failed to filter audit logs",
                isLoading = false
            )
        }
    }

    private fun clearError() {
        state = state.copy(error = null)
    }

    fun emitEvent(event: AdminEvent) {
        coroutineScope.launch {
            _events.emit(event)
        }
    }
}