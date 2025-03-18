package com.projecthub.ui.settings.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.projecthub.ui.components.Loading
import com.projecthub.ui.settings.model.*
import com.projecthub.ui.settings.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val state = viewModel.state
    var showDiscardDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (state.isDirty) {
                            showDiscardDialog = true
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.isDirty) {
                        TextButton(onClick = {
                            viewModel.emitEvent(SettingsEvent.SaveSettings)
                            onNavigateBack()
                        }) {
                            Text("Save")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Loading()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Appearance Section
                SettingsSection(title = "Appearance") {
                    ThemePreference(
                        currentTheme = state.settings.theme,
                        onThemeChanged = { theme ->
                            viewModel.emitEvent(SettingsEvent.UpdateTheme(theme))
                        }
                    )
                    
                    LanguagePreference(
                        currentLanguage = state.settings.language,
                        onLanguageChanged = { language ->
                            viewModel.emitEvent(SettingsEvent.UpdateLanguage(language))
                        }
                    )
                }

                // Notifications Section
                SettingsSection(title = "Notifications") {
                    NotificationPreferences(
                        settings = state.settings.notifications,
                        onSettingsChanged = { settings ->
                            viewModel.emitEvent(SettingsEvent.UpdateNotifications(settings))
                        }
                    )
                }

                // Sync Section
                SettingsSection(title = "Synchronization") {
                    SyncPreferences(
                        settings = state.settings.sync,
                        onSettingsChanged = { settings ->
                            viewModel.emitEvent(SettingsEvent.UpdateSync(settings))
                        }
                    )
                }

                // Reset Button
                Button(
                    onClick = { viewModel.emitEvent(SettingsEvent.ResetToDefaults) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("Reset to Defaults")
                }
            }
        }

        if (showDiscardDialog) {
            AlertDialog(
                onDismissRequest = { showDiscardDialog = false },
                title = { Text("Discard Changes?") },
                text = { Text("You have unsaved changes. Do you want to discard them?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDiscardDialog = false
                            onNavigateBack()
                        }
                    ) {
                        Text("Discard")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDiscardDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (state.error != null) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Error") },
                text = { Text(state.error) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.emitEvent(SettingsEvent.LoadSettings)
                    }) {
                        Text("Retry")
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun ThemePreference(
    currentTheme: ThemePreference,
    onThemeChanged: (ThemePreference) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = currentTheme.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Theme") },
            leadingIcon = { Icon(Icons.Default.Palette, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ThemePreference.values().forEach { theme ->
                DropdownMenuItem(
                    text = { Text(theme.name) },
                    onClick = {
                        onThemeChanged(theme)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun LanguagePreference(
    currentLanguage: String,
    onLanguageChanged: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val languages = remember { mapOf("en" to "English", "es" to "Español", "fr" to "Français") }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = languages[currentLanguage] ?: currentLanguage,
            onValueChange = {},
            readOnly = true,
            label = { Text("Language") },
            leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languages.forEach { (code, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onLanguageChanged(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun NotificationPreferences(
    settings: NotificationSettings,
    onSettingsChanged: (NotificationSettings) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Switch(
            checked = settings.enabled,
            onCheckedChange = { enabled ->
                onSettingsChanged(settings.copy(enabled = enabled))
            },
            thumbContent = {
                Icon(
                    imageVector = if (settings.enabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize)
                )
            }
        )

        if (settings.enabled) {
            CheckboxRow(
                text = "Task Reminders",
                checked = settings.taskReminders,
                onCheckedChange = { checked ->
                    onSettingsChanged(settings.copy(taskReminders = checked))
                }
            )
            
            CheckboxRow(
                text = "Project Updates",
                checked = settings.projectUpdates,
                onCheckedChange = { checked ->
                    onSettingsChanged(settings.copy(projectUpdates = checked))
                }
            )
            
            CheckboxRow(
                text = "Team Messages",
                checked = settings.teamMessages,
                onCheckedChange = { checked ->
                    onSettingsChanged(settings.copy(teamMessages = checked))
                }
            )
        }
    }
}

@Composable
private fun SyncPreferences(
    settings: SyncSettings,
    onSettingsChanged: (SyncSettings) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Switch(
            checked = settings.autoSync,
            onCheckedChange = { enabled ->
                onSettingsChanged(settings.copy(autoSync = enabled))
            },
            thumbContent = {
                Icon(
                    imageVector = if (settings.autoSync) Icons.Default.Sync else Icons.Default.SyncDisabled,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize)
                )
            }
        )

        if (settings.autoSync) {
            var showIntervalDialog by remember { mutableStateOf(false) }
            
            ListItem(
                headlineContent = { Text("Sync Interval") },
                supportingContent = { Text("${settings.syncInterval} minutes") },
                trailingContent = {
                    IconButton(onClick = { showIntervalDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Change Interval")
                    }
                }
            )
            
            if (showIntervalDialog) {
                var intervalText by remember { mutableStateOf(settings.syncInterval.toString()) }
                
                AlertDialog(
                    onDismissRequest = { showIntervalDialog = false },
                    title = { Text("Sync Interval") },
                    text = {
                        OutlinedTextField(
                            value = intervalText,
                            onValueChange = { text ->
                                intervalText = text.filter { it.isDigit() }
                            },
                            label = { Text("Minutes") },
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                intervalText.toIntOrNull()?.let { interval ->
                                    if (interval > 0) {
                                        onSettingsChanged(settings.copy(syncInterval = interval))
                                    }
                                }
                                showIntervalDialog = false
                            }
                        ) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showIntervalDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
            
            CheckboxRow(
                text = "Sync on Startup",
                checked = settings.syncOnStartup,
                onCheckedChange = { checked ->
                    onSettingsChanged(settings.copy(syncOnStartup = checked))
                }
            )
            
            CheckboxRow(
                text = "Sync only on Wi-Fi",
                checked = settings.syncOnWifi,
                onCheckedChange = { checked ->
                    onSettingsChanged(settings.copy(syncOnWifi = checked))
                }
            )
        }
    }
}

@Composable
private fun CheckboxRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text)
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}