package com.projecthub.compose.features.milestone

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MilestoneList(
    milestones: List<MilestoneDTO>,
    selectedMilestone: MilestoneDTO?,
    onMilestoneSelected: (MilestoneDTO) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(milestones) { milestone ->
                MilestoneListItem(
                    milestone = milestone,
                    isSelected = milestone.id == selectedMilestone?.id,
                    onClick = { onMilestoneSelected(milestone) }
                )
            }
        }
    }
}

@Composable
private fun MilestoneListItem(
    milestone: MilestoneDTO,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = if (isSelected) 
            MaterialTheme.colorScheme.primaryContainer 
        else 
            MaterialTheme.colorScheme.surface,
        tonalElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = milestone.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = milestone.status.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LinearProgressIndicator(
                    progress = milestone.progress / 100f,
                    modifier = Modifier.width(100.dp)
                )
            }
        }
    }
}