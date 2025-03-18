package com.projecthub.compose.features.milestone

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun MilestonesScreen(
    viewModel: MilestoneViewModel,
    onNavigateToMilestoneDetails: (UUID) -> Unit = {},
    selectedMilestoneId: String? = null,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state

    LaunchedEffect(selectedMilestoneId) {
        selectedMilestoneId?.let {
            viewModel.selectMilestone(UUID.fromString(it))
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (state.error != null) {
            ErrorBanner(
                message = state.error,
                onDismiss = viewModel::clearError
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // List of milestones (left panel)
            MilestoneList(
                milestones = state.milestones,
                selectedMilestone = state.selectedMilestone,
                onMilestoneSelected = { onNavigateToMilestoneDetails(it.id) },
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
            )

            // Details panel (right panel)
            state.selectedMilestone?.let { milestone ->
                MilestoneDetails(
                    milestone = milestone,
                    onStatusChange = viewModel::updateMilestoneStatus,
                    onProgressChange = viewModel::updateMilestoneProgress,
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight()
                        .padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun ErrorBanner(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            IconButton(onClick = onDismiss) {
                // TODO: Add close icon
            }
        }
    }
}