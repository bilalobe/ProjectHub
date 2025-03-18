package com.projecthub.ui.cohort.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.projecthub.ui.cohort.model.*
import com.projecthub.ui.cohort.viewmodel.CohortViewModel
import com.projecthub.ui.components.Loading
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CohortScreen(
    viewModel: CohortViewModel
) {
    val state by viewModel.state.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.emitEvent(CohortEvent.LoadCohorts)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cohort Management") },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Create Cohort")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                Loading()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.cohorts) { cohort ->
                        CohortCard(
                            cohort = cohort,
                            onClick = {
                                viewModel.emitEvent(CohortEvent.SelectCohort(cohort.id))
                            },
                            onStatusChange = { status ->
                                viewModel.emitEvent(CohortEvent.UpdateCohortStatus(cohort.id, status))
                            }
                        )
                    }
                }
            }

            if (showCreateDialog) {
                CreateCohortDialog(
                    onDismiss = { showCreateDialog = false },
                    onCreateCohort = { cohort ->
                        viewModel.emitEvent(CohortEvent.CreateCohort(cohort))
                        showCreateDialog = false
                    }
                )
            }

            state.selectedCohort?.let { cohort ->
                CohortDetailsSheet(
                    cohort = cohort,
                    onDismiss = {
                        viewModel.emitEvent(CohortEvent.SelectCohort(""))
                    },
                    onUpdateCohort = { updatedCohort ->
                        viewModel.emitEvent(CohortEvent.UpdateCohort(updatedCohort))
                    },
                    onEnrollStudent = { studentId ->
                        viewModel.emitEvent(CohortEvent.EnrollStudent(cohort.id, studentId))
                    },
                    onUpdateEnrollmentStatus = { studentId, status ->
                        viewModel.emitEvent(CohortEvent.UpdateEnrollmentStatus(cohort.id, studentId, status))
                    },
                    onAssignInstructor = { instructorId ->
                        viewModel.emitEvent(CohortEvent.AssignInstructor(cohort.id, instructorId))
                    },
                    onRemoveInstructor = { instructorId ->
                        viewModel.emitEvent(CohortEvent.RemoveInstructor(cohort.id, instructorId))
                    }
                )
            }

            state.error?.let { error ->
                AlertDialog(
                    onDismissRequest = {
                        viewModel.emitEvent(CohortEvent.ClearError)
                    },
                    title = { Text("Error") },
                    text = { Text(error) },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.emitEvent(CohortEvent.ClearError)
                        }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CohortCard(
    cohort: Cohort,
    onClick: () -> Unit,
    onStatusChange: (CohortStatus) -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cohort.name,
                    style = MaterialTheme.typography.titleMedium
                )
                AssistChip(
                    onClick = { showStatusMenu = true },
                    label = { Text(cohort.status.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (cohort.status) {
                            CohortStatus.PLANNED -> MaterialTheme.colorScheme.surfaceVariant
                            CohortStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer
                            CohortStatus.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer
                            CohortStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${cohort.term.type.name} ${cohort.term.year}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "${cohort.startDate.format(dateFormatter)} - ${cohort.endDate.format(dateFormatter)}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AssistChip(
                    onClick = {},
                    label = { Text("${cohort.students.size} Students") },
                    leadingIcon = {
                        Icon(Icons.Default.Group, contentDescription = null)
                    }
                )
                AssistChip(
                    onClick = {},
                    label = { Text("${cohort.instructors.size} Instructors") },
                    leadingIcon = {
                        Icon(Icons.Default.School, contentDescription = null)
                    }
                )
            }

            if (showStatusMenu) {
                AlertDialog(
                    onDismissRequest = { showStatusMenu = false },
                    title = { Text("Update Status") },
                    text = {
                        Column {
                            CohortStatus.values().forEach { status ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = status == cohort.status,
                                        onClick = {
                                            onStatusChange(status)
                                            showStatusMenu = false
                                        }
                                    )
                                    Text(
                                        text = status.name,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showStatusMenu = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}