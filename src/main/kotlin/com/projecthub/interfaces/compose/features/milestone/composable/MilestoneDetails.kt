package com.projecthub.ui.milestone.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.projecthub.base.milestone.api.dto.MilestoneDTO
import java.time.format.DateTimeFormatter
import java.time.LocalDate

@Composable
fun MilestoneDetails(
    milestone: MilestoneDTO,
    onUpdate: (MilestoneDTO) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Surface(
        modifier = modifier,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            if (isEditing) {
                MilestoneEditForm(
                    milestone = milestone,
                    onSave = { 
                        onUpdate(it)
                        isEditing = false
                    },
                    onCancel = { isEditing = false }
                )
            } else {
                MilestoneDetailsContent(
                    milestone = milestone,
                    onEditClick = { isEditing = true },
                    onDeleteClick = { showDeleteDialog = true }
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Milestone") },
            text = { Text("Are you sure you want to delete this milestone? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MilestoneDetailsContent(
    milestone: MilestoneDTO,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = milestone.name,
            style = MaterialTheme.typography.headlineMedium
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Milestone"
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Milestone"
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Surface(
        color = when(milestone.status) {
            MilestoneStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
            MilestoneStatus.IN_PROGRESS -> MaterialTheme.colorScheme.tertiaryContainer
            MilestoneStatus.BLOCKED -> MaterialTheme.colorScheme.errorContainer
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = milestone.status.displayName,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Due Date: ${milestone.dueDate.format(dateFormatter)}",
        style = MaterialTheme.typography.titleMedium
    )

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = milestone.description ?: "No description provided",
        style = MaterialTheme.typography.bodyLarge
    )

    if (milestone.dependencyIds.isNotEmpty()) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Dependencies",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // TODO: Add dependency list with links to dependent milestones
    }
}

@Composable
private fun MilestoneEditForm(
    milestone: MilestoneDTO,
    onSave: (MilestoneDTO) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(milestone.name) }
    var description by remember { mutableStateOf(milestone.description ?: "") }
    var dueDate by remember { mutableStateOf(milestone.dueDate) }
    var status by remember { mutableStateOf(milestone.status) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Add proper date picker
        OutlinedTextField(
            value = dueDate.toString(),
            onValueChange = { 
                try {
                    dueDate = LocalDate.parse(it)
                } catch (e: Exception) {
                    // Invalid date format
                }
            },
            label = { Text("Due Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Add proper status dropdown

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = {
                    onSave(milestone.copy(
                        name = name,
                        description = description.ifBlank { null },
                        dueDate = dueDate,
                        status = status
                    ))
                }
            ) {
                Text("Save")
            }
        }
    }
}