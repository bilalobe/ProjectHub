package com.projecthub.ui.dashboard.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.projecthub.ui.components.Loading
import com.projecthub.ui.dashboard.model.*
import com.projecthub.ui.dashboard.viewmodel.DashboardViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToProjects: () -> Unit,
    onNavigateToTeams: () -> Unit,
    onNavigateToUsers: () -> Unit
) {
    val state = viewModel.state

    LaunchedEffect(Unit) {
        viewModel.emitEvent(DashboardEvent.LoadDashboard)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Loading()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    MetricCards(
                        metrics = state.metrics,
                        onNavigateToProjects = onNavigateToProjects,
                        onNavigateToTeams = onNavigateToTeams,
                        onNavigateToUsers = onNavigateToUsers
                    )
                }

                item {
                    ProjectStatusChart(
                        distribution = state.metrics.projectStatusDistribution
                    )
                }

                if (state.projectMetrics.totalTasks > 0) {
                    item {
                        ProjectMetricsCard(metrics = state.projectMetrics)
                    }
                }

                item {
                    Text(
                        text = "Recent Activities",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(state.recentActivities) { activity ->
                    ActivityCard(activity = activity)
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
                        viewModel.emitEvent(DashboardEvent.ClearError)
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
private fun MetricCards(
    metrics: DashboardMetrics,
    onNavigateToProjects: () -> Unit,
    onNavigateToTeams: () -> Unit,
    onNavigateToUsers: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.weight(1f),
            onClick = onNavigateToProjects
        ) {
            MetricItem(
                icon = Icons.Default.Assignment,
                label = "Projects",
                value = metrics.totalProjects.toString()
            )
        }

        ElevatedCard(
            modifier = Modifier.weight(1f),
            onClick = onNavigateToTeams
        ) {
            MetricItem(
                icon = Icons.Default.Group,
                label = "Teams",
                value = metrics.totalTeams.toString()
            )
        }

        ElevatedCard(
            modifier = Modifier.weight(1f),
            onClick = onNavigateToUsers
        ) {
            MetricItem(
                icon = Icons.Default.Person,
                label = "Users",
                value = metrics.totalUsers.toString()
            )
        }
    }
}

@Composable
private fun MetricItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectStatusChart(distribution: List<ProjectStatusData>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Project Status Distribution",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                distribution.forEach { data ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = data.count.toString(),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = data.status,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectMetricsCard(metrics: ProjectDashboardMetrics) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Task Overview",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = if (metrics.totalTasks > 0) {
                    metrics.completedTasks.toFloat() / metrics.totalTasks
                } else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TaskMetric(
                    label = "Total",
                    value = metrics.totalTasks,
                    color = MaterialTheme.colorScheme.primary
                )
                TaskMetric(
                    label = "Completed",
                    value = metrics.completedTasks,
                    color = MaterialTheme.colorScheme.tertiary
                )
                TaskMetric(
                    label = "In Progress",
                    value = metrics.inProgressTasks,
                    color = MaterialTheme.colorScheme.secondary
                )
                TaskMetric(
                    label = "Pending",
                    value = metrics.pendingTasks,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun TaskMetric(
    label: String,
    value: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivityCard(activity: RecentActivity) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, HH:mm") }

    ListItem(
        headlineContent = {
            Text(activity.activity)
        },
        supportingContent = {
            Text(
                text = "by ${activity.user} • ${activity.timestamp.format(dateFormatter)}",
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingContent = {
            Icon(
                imageVector = when (activity.type) {
                    ActivityType.PROJECT_CREATED -> Icons.Default.AddCircle
                    ActivityType.TASK_COMPLETED -> Icons.Default.CheckCircle
                    ActivityType.TEAM_UPDATED -> Icons.Default.Group
                    ActivityType.SUBMISSION_GRADED -> Icons.Default.Grade
                    ActivityType.USER_JOINED -> Icons.Default.PersonAdd
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}