package com.projecthub.compose.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.projecthub.compose.components.StatCard
import com.projecthub.compose.theme.ProjectHubBlue
import com.projecthub.compose.theme.ProjectHubGreen
import com.projecthub.compose.theme.ProjectHubOrange
import com.projecthub.compose.theme.ProjectHubRed
import com.projecthub.compose.util.platformPadding
import com.projecthub.compose.viewmodel.DashboardViewModel

/**
 * A dashboard screen that displays key statistics and recent activities.
 * This is a shared screen component that can be used in both mobile and desktop UIs.
 * 
 * @param viewModel The dashboard view model
 * @param modifier Additional modifiers
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    // Collect state from the ViewModel
    val totalUsers by viewModel.totalUsers.collectAsState()
    val totalProjects by viewModel.totalProjects.collectAsState()
    val totalTeams by viewModel.totalTeams.collectAsState()
    val recentActivities by viewModel.recentActivities.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Load data when the screen is first displayed
    LaunchedEffect(key1 = Unit) {
        viewModel.loadDashboardData()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Main content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(platformPadding()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Dashboard Overview",
                    style = MaterialTheme.typography.h5
                )
            }

            // Stats cards section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        title = "Total Users",
                        value = totalUsers.toString(),
                        icon = Icons.Filled.Person,
                        iconTint = ProjectHubBlue,
                        modifier = Modifier.weight(1f)
                    )
                    
                    StatCard(
                        title = "Total Projects",
                        value = totalProjects.toString(),
                        icon = Icons.Filled.Folder,
                        iconTint = ProjectHubGreen,
                        modifier = Modifier.weight(1f)
                    )
                    
                    StatCard(
                        title = "Total Teams",
                        value = totalTeams.toString(),
                        icon = Icons.Filled.Group,
                        iconTint = ProjectHubOrange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Recent activities section
            item {
                Text(
                    text = "Recent Activities",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            
            if (recentActivities.isNotEmpty()) {
                items(recentActivities) { activity ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = activity.activity,
                                style = MaterialTheme.typography.body1
                            )
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = activity.user,
                                    style = MaterialTheme.typography.caption
                                )
                                
                                Text(
                                    text = formatTimestamp(activity.timestamp),
                                    style = MaterialTheme.typography.caption
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 1.dp
                    ) {
                        Text(
                            text = "No recent activities",
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
        
        // Loading indicator
        if (isLoading) {
            CircularProgressIndicator()
        }
        
        // Error message
        error?.let { errorMsg ->
            Card(
                backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Error,
                        contentDescription = null,
                        tint = ProjectHubRed
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = errorMsg,
                        style = MaterialTheme.typography.body2,
                        color = ProjectHubRed
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    IconButton(onClick = { viewModel.loadDashboardData() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Retry",
                            tint = ProjectHubRed
                        )
                    }
                }
            }
        }
    }
}

// Helper function to format timestamp
private fun formatTimestamp(timestamp: String): String {
    // In a real implementation, you would parse the timestamp and format it
    // For now, we'll just return it as is
    return timestamp
}