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
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CohortDetailsSheet(
    cohort: Cohort,
    onDismiss: () -> Unit,
    onUpdateCohort: (Cohort) -> Unit,
    onEnrollStudent: (String) -> Unit,
    onUpdateEnrollmentStatus: (String, EnrollmentStatus) -> Unit,
    onAssignInstructor: (String) -> Unit,
    onRemoveInstructor: (String) -> Unit
) {
    var currentTab by remember { mutableStateOf(0) }
    val tabs = listOf("Details", "Students", "Instructors")

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = cohort.name,
                style = MaterialTheme.typography.headlineMedium
            )

            TabRow(
                selectedTabIndex = currentTab,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = currentTab == index,
                        onClick = { currentTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (currentTab) {
                0 -> CohortDetailsTab(
                    cohort = cohort,
                    onUpdateCohort = onUpdateCohort
                )
                1 -> CohortStudentsTab(
                    students = cohort.students,
                    onEnrollStudent = onEnrollStudent,
                    onUpdateStatus = onUpdateEnrollmentStatus
                )
                2 -> CohortInstructorsTab(
                    instructors = cohort.instructors,
                    onAssignInstructor = onAssignInstructor,
                    onRemoveInstructor = onRemoveInstructor
                )
            }
        }
    }
}

@Composable
private fun CohortDetailsTab(
    cohort: Cohort,
    onUpdateCohort: (Cohort) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedCohort by remember { mutableStateOf(cohort) }
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    Column(modifier = Modifier.fillMaxWidth()) {
        if (!isEditing) {
            Text(
                text = "Term: ${cohort.term.type.name} ${cohort.term.year}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Duration: ${cohort.startDate.format(dateFormatter)} - ${cohort.endDate.format(dateFormatter)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Status: ${cohort.status}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Capacity: ${cohort.capacity}",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = { isEditing = true },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Edit Details")
            }
        } else {
            // Edit form implementation would go here
            // For brevity, we'll just show a save button
            Button(
                onClick = {
                    onUpdateCohort(editedCohort)
                    isEditing = false
                }
            ) {
                Text("Save Changes")
            }
        }
    }
}

@Composable
private fun CohortStudentsTab(
    students: List<CohortStudent>,
    onEnrollStudent: (String) -> Unit,
    onUpdateStatus: (String, EnrollmentStatus) -> Unit
) {
    var showEnrollDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { showEnrollDialog = true },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Enroll Student")
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(students) { student ->
                StudentEnrollmentCard(
                    student = student,
                    onUpdateStatus = { status ->
                        onUpdateStatus(student.id, status)
                    }
                )
            }
        }

        if (showEnrollDialog) {
            // Enrollment dialog implementation would go here
            // For brevity, we'll leave this out
        }
    }
}

@Composable
private fun CohortInstructorsTab(
    instructors: List<CohortInstructor>,
    onAssignInstructor: (String) -> Unit,
    onRemoveInstructor: (String) -> Unit
) {
    var showAssignDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { showAssignDialog = true },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Assign Instructor")
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(instructors) { instructor ->
                InstructorCard(
                    instructor = instructor,
                    onRemove = { onRemoveInstructor(instructor.id) }
                )
            }
        }

        if (showAssignDialog) {
            // Instructor assignment dialog implementation would go here
            // For brevity, we'll leave this out
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentEnrollmentCard(
    student: CohortStudent,
    onUpdateStatus: (EnrollmentStatus) -> Unit
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = student.email,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            AssistChip(
                onClick = { showStatusMenu = true },
                label = { Text(student.status.name) }
            )
        }
    }

    if (showStatusMenu) {
        AlertDialog(
            onDismissRequest = { showStatusMenu = false },
            title = { Text("Update Enrollment Status") },
            text = {
                Column {
                    EnrollmentStatus.values().forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = status == student.status,
                                onClick = {
                                    onUpdateStatus(status)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InstructorCard(
    instructor: CohortInstructor,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = instructor.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = instructor.email,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove Instructor"
                )
            }
        }
    }
}