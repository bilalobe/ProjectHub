package com.projecthub.ui.admin.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.projecthub.ui.admin.model.*
import com.projecthub.ui.admin.viewmodel.AdminViewModel
import com.projecthub.ui.components.Loading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel
) {
    val state = viewModel.state

    LaunchedEffect(Unit) {
        viewModel.emitEvent(AdminEvent.LoadSettings)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Administration") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = state.selectedTab.ordinal,
                modifier = Modifier.fillMaxWidth()
            ) {
                AdminTab.values().forEach { tab ->
                    Tab(
                        selected = state.selectedTab == tab,
                        onClick = {
                            viewModel.emitEvent(AdminEvent.SelectTab(tab))
                        },
                        text = { Text(tab.name.lowercase().capitalize()) },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    AdminTab.SETTINGS -> Icons.Default.Settings
                                    AdminTab.USERS -> Icons.Default.ManageAccounts
                                    AdminTab.AUDIT -> Icons.Default.History
                                    AdminTab.HEALTH -> Icons.Default.HealthAndSafety
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            if (state.isLoading) {
                Loading()
            } else {
                when (state.selectedTab) {
                    AdminTab.SETTINGS -> SystemSettingsTab(
                        settings = state.settings,
                        onUpdateSettings = { settings ->
                            viewModel.emitEvent(AdminEvent.UpdateSettings(settings))
                        }
                    )
                    AdminTab.USERS -> UserManagementTab(
                        users = state.users,
                        onUpdateRole = { userId, roles ->
                            viewModel.emitEvent(AdminEvent.UpdateUserRole(userId, roles))
                        },
                        onUpdateStatus = { userId, status ->
                            viewModel.emitEvent(AdminEvent.UpdateUserStatus(userId, status))
                        }
                    )
                    AdminTab.AUDIT -> AuditLogTab(
                        logs = state.auditLogs,
                        onFilter = { startDate, endDate, category ->
                            viewModel.emitEvent(
                                AdminEvent.FilterAuditLogs(startDate, endDate, category)
                            )
                        }
                    )
                    AdminTab.HEALTH -> SystemHealthTab(
                        health = state.systemHealth
                    )
                }
            }
        }

        if (state.error != null) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Error") },
                text = { Text(state.error!!) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.emitEvent(AdminEvent.ClearError)
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SystemSettingsTab(
    settings: SystemSettings,
    onUpdateSettings: (SystemSettings) -> Unit
) {
    var currentSettings by remember(settings) { mutableStateOf(settings) }
    var showSaveDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "System Settings",
                        style = MaterialTheme.typography.titleLarge
                    )

                    OutlinedTextField(
                        value = currentSettings.maxTeamSize.toString(),
                        onValueChange = { value ->
                            value.toIntOrNull()?.let {
                                currentSettings = currentSettings.copy(maxTeamSize = it)
                            }
                        },
                        label = { Text("Max Team Size") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = currentSettings.maxProjectsPerUser.toString(),
                        onValueChange = { value ->
                            value.toIntOrNull()?.let {
                                currentSettings = currentSettings.copy(maxProjectsPerUser = it)
                            }
                        },
                        label = { Text("Max Projects per User") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = currentSettings.sessionTimeoutMinutes.toString(),
                        onValueChange = { value ->
                            value.toIntOrNull()?.let {
                                currentSettings = currentSettings.copy(sessionTimeoutMinutes = it)
                            }
                        },
                        label = { Text("Session Timeout (minutes)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("User Registration")
                        Switch(
                            checked = currentSettings.userRegistrationEnabled,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(userRegistrationEnabled = it)
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Email Notifications")
                        Switch(
                            checked = currentSettings.emailNotificationsEnabled,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(emailNotificationsEnabled = it)
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Maintenance Mode")
                        Switch(
                            checked = currentSettings.maintenanceMode,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(maintenanceMode = it)
                            }
                        )
                    }

                    if (currentSettings.maintenanceMode) {
                        OutlinedTextField(
                            value = currentSettings.maintenanceMessage,
                            onValueChange = { value ->
                                currentSettings = currentSettings.copy(maintenanceMessage = value)
                            },
                            label = { Text("Maintenance Message") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showSaveDialog = true }
                        ) {
                            Text("Save Changes")
                        }
                    }
                }
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Changes") },
            text = { Text("Are you sure you want to update the system settings?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onUpdateSettings(currentSettings)
                        showSaveDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun UserManagementTab(
    users: List<AdminUserInfo>,
    onUpdateRole: (String, Set<UserRole>) -> Unit,
    onUpdateStatus: (String, UserStatus) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(users) { user ->
            UserCard(
                user = user,
                onUpdateRole = onUpdateRole,
                onUpdateStatus = onUpdateStatus
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserCard(
    user: AdminUserInfo,
    onUpdateRole: (String, Set<UserRole>) -> Unit,
    onUpdateStatus: (String, UserStatus) -> Unit
) {
    var showRoleDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = user.username,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                AssistChip(
                    onClick = { showStatusDialog = true },
                    label = { Text(user.status.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (user.status) {
                            UserStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer
                            UserStatus.INACTIVE -> MaterialTheme.colorScheme.surfaceVariant
                            UserStatus.SUSPENDED -> MaterialTheme.colorScheme.errorContainer
                            UserStatus.PENDING_VERIFICATION -> MaterialTheme.colorScheme.tertiaryContainer
                        }
                    )
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                user.roles.forEach { role ->
                    AssistChip(
                        onClick = { showRoleDialog = true },
                        label = { Text(role.name) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { showRoleDialog = true }) {
                    Text("Edit Roles")
                }
                TextButton(onClick = { showStatusDialog = true }) {
                    Text("Change Status")
                }
            }
        }
    }

    if (showRoleDialog) {
        RoleSelectionDialog(
            currentRoles = user.roles,
            onDismiss = { showRoleDialog = false },
            onConfirm = { roles ->
                onUpdateRole(user.id, roles)
                showRoleDialog = false
            }
        )
    }

    if (showStatusDialog) {
        StatusSelectionDialog(
            currentStatus = user.status,
            onDismiss = { showStatusDialog = false },
            onConfirm = { status ->
                onUpdateStatus(user.id, status)
                showStatusDialog = false
            }
        )
    }
}

@Composable
private fun RoleSelectionDialog(
    currentRoles: Set<UserRole>,
    onDismiss: () -> Unit,
    onConfirm: (Set<UserRole>) -> Unit
) {
    var selectedRoles by remember { mutableStateOf(currentRoles) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Roles") },
        text = {
            Column {
                UserRole.values().forEach { role ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(role.name)
                        Checkbox(
                            checked = selectedRoles.contains(role),
                            onCheckedChange = { checked ->
                                selectedRoles = if (checked) {
                                    selectedRoles + role
                                } else {
                                    selectedRoles - role
                                }
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedRoles) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun StatusSelectionDialog(
    currentStatus: UserStatus,
    onDismiss: () -> Unit,
    onConfirm: (UserStatus) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(currentStatus) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Status") },
        text = {
            Column {
                UserStatus.values().forEach { status ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                        Text(status.name)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedStatus) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuditLogTab(
    logs: List<AuditLogEntry>,
    onFilter: (LocalDateTime?, LocalDateTime?, AuditCategory?) -> Unit
) {
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<AuditCategory?>(null) }
    val dateFormatter = remember { java.time.format.DateTimeFormatter.ofPattern("MMM dd, HH:mm") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Audit Logs",
                style = MaterialTheme.typography.titleLarge
            )
            FilledTonalIconButton(
                onClick = { showFilterDialog = true }
            ) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(logs) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text(log.action) },
                        supportingContent = {
                            Text(
                                "${log.user} • ${log.timestamp.format(dateFormatter)} • ${log.ipAddress}"
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = when (log.category) {
                                    AuditCategory.USER_MANAGEMENT -> Icons.Default.ManageAccounts
                                    AuditCategory.SYSTEM_SETTINGS -> Icons.Default.Settings
                                    AuditCategory.SECURITY -> Icons.Default.Security
                                    AuditCategory.PROJECT -> Icons.Default.Assignment
                                    AuditCategory.TEAM -> Icons.Default.Group
                                    AuditCategory.DATA -> Icons.Default.Storage
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter Logs") },
            text = {
                Column {
                    // In a real implementation, we would add date range pickers here
                    Text("Filter by Category:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        AuditCategory.values().forEach { category ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                RadioButton(
                                    selected = selectedCategory == category,
                                    onClick = { selectedCategory = category }
                                )
                                Text(category.name)
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RadioButton(
                                selected = selectedCategory == null,
                                onClick = { selectedCategory = null }
                            )
                            Text("All Categories")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onFilter(null, null, selectedCategory)
                        showFilterDialog = false
                    }
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SystemHealthTab(health: SystemHealth) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "System Status",
                            style = MaterialTheme.typography.titleMedium
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text(health.status.name) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = when (health.status) {
                                    SystemStatus.HEALTHY -> MaterialTheme.colorScheme.primaryContainer
                                    SystemStatus.DEGRADED -> MaterialTheme.colorScheme.tertiaryContainer
                                    SystemStatus.DOWN -> MaterialTheme.colorScheme.errorContainer
                                    SystemStatus.UNKNOWN -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "System Metrics",
                        style = MaterialTheme.typography.titleMedium
                    )

                    MetricRow("CPU Usage", health.metrics.cpuUsage, "%")
                    MetricRow("Memory Usage", health.metrics.memoryUsage, "%")
                    MetricRow("Disk Usage", health.metrics.diskUsage, "%")
                    MetricRow("Active Users", health.metrics.activeUsers.toDouble(), "")
                    MetricRow("Requests/min", health.metrics.requestsPerMinute.toDouble(), "")
                    MetricRow("Avg Response Time", health.metrics.averageResponseTime, "ms")
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Service Status",
                        style = MaterialTheme.typography.titleMedium
                    )

                    health.services.forEach { service ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = service.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (service.lastError != null) {
                                    Text(
                                        text = service.lastError,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            AssistChip(
                                onClick = {},
                                label = { Text(service.status.name) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = when (service.status) {
                                        SystemStatus.HEALTHY -> MaterialTheme.colorScheme.primaryContainer
                                        SystemStatus.DEGRADED -> MaterialTheme.colorScheme.tertiaryContainer
                                        SystemStatus.DOWN -> MaterialTheme.colorScheme.errorContainer
                                        SystemStatus.UNKNOWN -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: Double,
    unit: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(
            buildString {
                append(String.format("%.1f", value))
                if (unit.isNotEmpty()) {
                    append(" ")
                    append(unit)
                }
            }
        )
    }
}

@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit
) {
    // This is a simplified flow row implementation
    // In a real app, we would use a proper flow layout
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement
    ) {
        content()
    }
}