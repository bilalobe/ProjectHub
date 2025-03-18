package com.projecthub.compose.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.projecthub.compose.features.projects.Project
import com.projecthub.compose.features.projects.ProjectStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectCard(
    project: Project,
    onClick: () -> Unit,
    onStatusChange: (ProjectStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header with project name and menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                // Status menu
                var expanded by remember { mutableStateOf(false) }
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, "More options")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    ProjectStatus.values().forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.name.replace("_", " ").lowercase().capitalize()) },
                            leadingIcon = {
                                if (project.status == status) {
                                    Icon(Icons.Default.Check, null)
                                }
                            },
                            onClick = {
                                onStatusChange(status)
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            // Description
            Text(
                text = project.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Progress bar
            val animatedProgress by animateFloatAsState(
                targetValue = project.progress,
                label = "Progress"
            )
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth(),
                color = when (project.status) {
                    ProjectStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                    ProjectStatus.IN_PROGRESS -> MaterialTheme.colorScheme.secondary
                    ProjectStatus.ON_HOLD -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
            
            // Metadata row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team size
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${project.teamSize}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Due date
                project.dueDate?.let { dueDate ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = SimpleDateFormat("MMM dd", Locale.getDefault()).format(dueDate),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Status chip
                StatusChip(status = project.status)
            }
            
            // Tags
            if (project.tags.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(project.tags) { tag ->
                        TagChip(tag = tag)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(
    status: ProjectStatus,
    modifier: Modifier = Modifier
) {
    val (containerColor, contentColor) = when (status) {
        ProjectStatus.PLANNING -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        ProjectStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        ProjectStatus.ON_HOLD -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        ProjectStatus.COMPLETED -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        ProjectStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }
    
    Surface(
        color = containerColor,
        modifier = modifier.height(24.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.name.replace("_", " ").lowercase().capitalize(),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun TagChip(
    tag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.height(24.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}