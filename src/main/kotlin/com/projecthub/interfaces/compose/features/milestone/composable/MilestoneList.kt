package com.projecthub.ui.milestone.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.projecthub.base.milestone.api.dto.MilestoneDTO
import java.time.format.DateTimeFormatter

@Composable
fun MilestoneList(
    milestones: List<MilestoneDTO>,
    selectedMilestone: MilestoneDTO?,
    onMilestoneSelect: (MilestoneDTO) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        tonalElevation = 1.dp
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(milestones) { milestone ->
                MilestoneListItem(
                    milestone = milestone,
                    isSelected = milestone.id == selectedMilestone?.id,
                    onClick = { onMilestoneSelect(milestone) }
                )
            }
        }
    }
}

@Composable
private fun MilestoneListItem(
    milestone: MilestoneDTO,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        tonalElevation = if (isSelected) 2.dp else 0.dp,
        color = if (isSelected) 
            MaterialTheme.colorScheme.secondaryContainer
        else
            MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = milestone.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Due: ${milestone.dueDate.format(dateFormatter)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Surface(
                    color = when(milestone.status) {
                        MilestoneStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
                        MilestoneStatus.IN_PROGRESS -> MaterialTheme.colorScheme.tertiaryContainer
                        MilestoneStatus.BLOCKED -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = milestone.status.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            if (milestone.description?.isNotBlank() == true) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = milestone.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}