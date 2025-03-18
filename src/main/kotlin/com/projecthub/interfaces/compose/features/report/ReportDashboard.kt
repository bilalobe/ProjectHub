package com.projecthub.ui.report

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.projecthub.ui.components.*
import com.projecthub.ui.theme.ProjectHubTheme

@Composable
fun ReportDashboard(
    viewModel: ReportViewModel,
    modifier: Modifier = Modifier
) {
    ProjectHubTheme {
        val state by viewModel.state.collectAsState()

        LoadingOverlay(isLoading = state.isLoading) {
            PageContainer(
                title = "Analytics",
                modifier = modifier,
                actions = {
                    SecondaryButton(
                        text = "Refresh",
                        onClick = { viewModel.handleIntent(ReportIntent.RefreshData) }
                    )
                    PrimaryButton(
                        text = "Export Report",
                        onClick = { viewModel.handleIntent(ReportIntent.ExportReport) }
                    )
                }
            ) {
                GridContainer(columns = 3) {
                    StatisticCard(
                        title = "Total Users",
                        value = state.totalUsers.toString(),
                        color = MaterialTheme.colorScheme.primary
                    )
                    StatisticCard(
                        title = "Total Projects",
                        value = state.totalProjects.toString(),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    StatisticCard(
                        title = "Total Teams",
                        value = state.totalTeams.toString(),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                ContentCard(
                    title = "Project Status Distribution",
                    modifier = Modifier.height(300.dp)
                ) {
                    ProjectStatusChart(
                        data = state.projectStatusDistribution,
                        modifier = Modifier.weight(1f)
                    )
                }

                ContentCard(
                    title = "Recent Activity",
                    titleActions = {
                        DateRangeSelector(
                            currentRange = state.selectedDateRange,
                            onRangeSelected = { range ->
                                viewModel.handleIntent(ReportIntent.FilterByDateRange(range))
                            }
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    ActivityTimeline(
                        activities = state.recentActivities,
                        modifier = Modifier.weight(1f)
                    )
                }

                state.error?.let { error ->
                    StatusSnackbar(
                        message = error,
                        isError = true
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectStatusChart(
    data: List<ProjectStatusDTO>,
    modifier: Modifier = Modifier
) {
    // TODO: Implement chart visualization using a charting library
    Column(modifier = modifier) {
        data.forEach { status ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(status.status)
                Text(status.count.toString())
            }
        }
    }
}

@Composable
private fun DateRangeSelector(
    currentRange: Pair<String, String>?,
    onRangeSelected: (Pair<String, String>) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    
    SecondaryButton(
        text = currentRange?.let { (start, end) ->
            "$start - $end"
        } ?: "Select Date Range",
        onClick = { showDialog = true }
    )

    if (showDialog) {
        // TODO: Implement date range picker dialog
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        onRangeSelected(
            Pair(
                now.minusDays(7).format(formatter),
                now.format(formatter)
            )
        )
        showDialog = false
    }
}

@Composable
private fun ActivityTimeline(
    activities: List<ActivityDTO>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(activities) { activity ->
            StandardCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = activity.activity,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "by ${activity.user}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = activity.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}