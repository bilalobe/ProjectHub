package com.projecthub.ui.settings.model

data class AppSettings(
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val language: String = "en",
    val notifications: NotificationSettings = NotificationSettings(),
    val sync: SyncSettings = SyncSettings()
)

data class NotificationSettings(
    val enabled: Boolean = true,
    val taskReminders: Boolean = true,
    val projectUpdates: Boolean = true,
    val teamMessages: Boolean = true
)

data class SyncSettings(
    val autoSync: Boolean = true,
    val syncInterval: Int = 15, // minutes
    val syncOnStartup: Boolean = true,
    val syncOnWifi: Boolean = true
)

enum class ThemePreference {
    LIGHT,
    DARK,
    SYSTEM
}

data class SettingsState(
    val settings: AppSettings = AppSettings(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDirty: Boolean = false
)

sealed class SettingsEvent {
    data class UpdateTheme(val theme: ThemePreference) : SettingsEvent()
    data class UpdateLanguage(val language: String) : SettingsEvent()
    data class UpdateNotifications(val settings: NotificationSettings) : SettingsEvent()
    data class UpdateSync(val settings: SyncSettings) : SettingsEvent()
    object SaveSettings : SettingsEvent()
    object LoadSettings : SettingsEvent()
    object ResetToDefaults : SettingsEvent()
}