package com.projecthub.ui.admin.model

import java.time.LocalDateTime

data class AdminState(
    val settings: SystemSettings = SystemSettings(),
    val users: List<AdminUserInfo> = emptyList(),
    val auditLogs: List<AuditLogEntry> = emptyList(),
    val systemHealth: SystemHealth = SystemHealth(),
    val selectedTab: AdminTab = AdminTab.SETTINGS,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class SystemSettings(
    val userRegistrationEnabled: Boolean = true,
    val maxTeamSize: Int = 10,
    val maxProjectsPerUser: Int = 5,
    val maxFileUploadSize: Long = 10 * 1024 * 1024, // 10MB
    val allowedFileTypes: Set<String> = setOf("pdf", "doc", "docx", "txt"),
    val emailNotificationsEnabled: Boolean = true,
    val sessionTimeoutMinutes: Int = 30,
    val maintenanceMode: Boolean = false,
    val maintenanceMessage: String = ""
)

data class AdminUserInfo(
    val id: String,
    val username: String,
    val email: String,
    val roles: Set<UserRole>,
    val status: UserStatus,
    val lastLogin: LocalDateTime?,
    val createdAt: LocalDateTime
)

enum class UserRole {
    ADMIN,
    INSTRUCTOR,
    STUDENT,
    GUEST
}

enum class UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    PENDING_VERIFICATION
}

data class AuditLogEntry(
    val id: String,
    val timestamp: LocalDateTime,
    val user: String,
    val action: String,
    val category: AuditCategory,
    val details: String,
    val ipAddress: String
)

enum class AuditCategory {
    USER_MANAGEMENT,
    SYSTEM_SETTINGS,
    SECURITY,
    PROJECT,
    TEAM,
    DATA
}

data class SystemHealth(
    val status: SystemStatus = SystemStatus.UNKNOWN,
    val metrics: SystemMetrics = SystemMetrics(),
    val services: List<ServiceStatus> = emptyList(),
    val lastChecked: LocalDateTime = LocalDateTime.now()
)

data class SystemMetrics(
    val cpuUsage: Double = 0.0,
    val memoryUsage: Double = 0.0,
    val diskUsage: Double = 0.0,
    val activeUsers: Int = 0,
    val requestsPerMinute: Int = 0,
    val averageResponseTime: Double = 0.0
)

data class ServiceStatus(
    val name: String,
    val status: SystemStatus,
    val uptime: Long,
    val lastError: String?
)

enum class SystemStatus {
    HEALTHY,
    DEGRADED,
    DOWN,
    UNKNOWN
}

enum class AdminTab {
    SETTINGS,
    USERS,
    AUDIT,
    HEALTH
}

sealed class AdminEvent {
    object LoadSettings : AdminEvent()
    object LoadUsers : AdminEvent()
    object LoadAuditLogs : AdminEvent()
    object LoadSystemHealth : AdminEvent()
    data class UpdateSettings(val settings: SystemSettings) : AdminEvent()
    data class UpdateUserRole(val userId: String, val roles: Set<UserRole>) : AdminEvent()
    data class UpdateUserStatus(val userId: String, val status: UserStatus) : AdminEvent()
    data class SelectTab(val tab: AdminTab) : AdminEvent()
    data class FilterAuditLogs(
        val startDate: LocalDateTime?,
        val endDate: LocalDateTime?,
        val category: AuditCategory?
    ) : AdminEvent()
    object ClearError : AdminEvent()
}