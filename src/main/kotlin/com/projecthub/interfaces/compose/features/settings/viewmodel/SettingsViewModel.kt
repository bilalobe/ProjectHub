package com.projecthub.ui.settings.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.projecthub.ui.settings.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.prefs.Preferences

class SettingsViewModel(
    private val coroutineScope: CoroutineScope
) {
    var state by mutableStateOf(SettingsState())
        private set

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events: SharedFlow<SettingsEvent> = _events.asSharedFlow()

    private val prefs = Preferences.userRoot().node("com.projecthub.settings")

    init {
        coroutineScope.launch {
            _events.collect { event ->
                handleEvent(event)
            }
        }
        loadSettings()
    }

    private suspend fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.UpdateTheme -> updateTheme(event.theme)
            is SettingsEvent.UpdateLanguage -> updateLanguage(event.language)
            is SettingsEvent.UpdateNotifications -> updateNotifications(event.settings)
            is SettingsEvent.UpdateSync -> updateSync(event.settings)
            is SettingsEvent.SaveSettings -> saveSettings()
            is SettingsEvent.LoadSettings -> loadSettings()
            is SettingsEvent.ResetToDefaults -> resetToDefaults()
        }
    }

    private fun updateTheme(theme: ThemePreference) {
        state = state.copy(
            settings = state.settings.copy(theme = theme),
            isDirty = true
        )
    }

    private fun updateLanguage(language: String) {
        state = state.copy(
            settings = state.settings.copy(language = language),
            isDirty = true
        )
    }

    private fun updateNotifications(settings: NotificationSettings) {
        state = state.copy(
            settings = state.settings.copy(notifications = settings),
            isDirty = true
        )
    }

    private fun updateSync(settings: SyncSettings) {
        state = state.copy(
            settings = state.settings.copy(sync = settings),
            isDirty = true
        )
    }

    private fun loadSettings() {
        state = state.copy(isLoading = true)
        try {
            val theme = ThemePreference.valueOf(
                prefs.get("theme", ThemePreference.SYSTEM.name)
            )
            val language = prefs.get("language", "en")
            
            val notifications = NotificationSettings(
                enabled = prefs.getBoolean("notifications.enabled", true),
                taskReminders = prefs.getBoolean("notifications.taskReminders", true),
                projectUpdates = prefs.getBoolean("notifications.projectUpdates", true),
                teamMessages = prefs.getBoolean("notifications.teamMessages", true)
            )
            
            val sync = SyncSettings(
                autoSync = prefs.getBoolean("sync.autoSync", true),
                syncInterval = prefs.getInt("sync.interval", 15),
                syncOnStartup = prefs.getBoolean("sync.onStartup", true),
                syncOnWifi = prefs.getBoolean("sync.onWifi", true)
            )

            state = state.copy(
                settings = AppSettings(
                    theme = theme,
                    language = language,
                    notifications = notifications,
                    sync = sync
                ),
                isLoading = false,
                isDirty = false
            )
        } catch (e: Exception) {
            state = state.copy(
                error = "Failed to load settings: ${e.message}",
                isLoading = false
            )
        }
    }

    private fun saveSettings() {
        state = state.copy(isLoading = true)
        try {
            with(state.settings) {
                prefs.put("theme", theme.name)
                prefs.put("language", language)
                
                with(notifications) {
                    prefs.putBoolean("notifications.enabled", enabled)
                    prefs.putBoolean("notifications.taskReminders", taskReminders)
                    prefs.putBoolean("notifications.projectUpdates", projectUpdates)
                    prefs.putBoolean("notifications.teamMessages", teamMessages)
                }
                
                with(sync) {
                    prefs.putBoolean("sync.autoSync", autoSync)
                    prefs.putInt("sync.interval", syncInterval)
                    prefs.putBoolean("sync.onStartup", syncOnStartup)
                    prefs.putBoolean("sync.onWifi", syncOnWifi)
                }
            }
            
            prefs.flush()
            state = state.copy(isLoading = false, isDirty = false)
        } catch (e: Exception) {
            state = state.copy(
                error = "Failed to save settings: ${e.message}",
                isLoading = false
            )
        }
    }

    private fun resetToDefaults() {
        state = state.copy(
            settings = AppSettings(),
            isDirty = true
        )
    }

    fun emitEvent(event: SettingsEvent) {
        coroutineScope.launch {
            _events.emit(event)
        }
    }
}