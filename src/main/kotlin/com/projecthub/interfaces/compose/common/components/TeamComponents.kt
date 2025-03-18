package com.projecthub.compose.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.projecthub.compose.features.teams.Team
import com.projecthub.compose.features.teams.TeamStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamCard(
    team: Team,
    onClick: () -> Unit,
    onStatusChange: (TeamStatus) -> Unit,
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
            // Header with team name and menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = team.name,
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
                    TeamStatus.values().forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.name.replace("_", " ").lowercase().capitalize()) },
                            leadingIcon = {
                                if (team.status == status) {
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
                text = team.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Member count
                StatItem(
                    icon = Icons.Default.Group,
                    value = team.memberCount.toString(),
                    label = "members"
                )
                
                // Active projects
                StatItem(
                    icon = Icons.Default.FolderOpen,
                    value = team.activeProjects.toString(),
                    label = "active projects"
                )
                
                // Completed projects
                StatItem(
                    icon = Icons.Default.Done,
                    value = team.completedProjects.toString(),
                    label = "completed"
                )
            }
            
            // Status and last active
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamStatusChip(status = team.status)
                
                // Last active
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Active ${formatTimeAgo(team.lastActive)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$value $label",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TeamStatusChip(
    status: TeamStatus,
    modifier: Modifier = Modifier
) {
    val (containerColor, contentColor) = when (status) {
        TeamStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        TeamStatus.INACTIVE -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        TeamStatus.ARCHIVED -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Surface(
        color = containerColor,
        modifier = modifier.height(24.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.name.lowercase().capitalize(),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun formatTimeAgo(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> "just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        diff < 604800_000 -> "${diff / 86400_000}d ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
    }
}