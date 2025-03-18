package com.projecthub.ui.cohort

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.projecthub.ui.cohort.components.*
import com.projecthub.ui.shared.components.LoadingIndicator
import com.projecthub.ui.shared.components.ErrorMessage
import java.util.*

@Composable
fun CohortSeatingScreen(
    viewModel: CohortSeatingViewModel,
    cohortId: UUID,
    onBackClick: () -> Unit,
    onTeamClick: (UUID) -> Unit,
    modifier: Modifier = Modifier
) {
    // Load cohort data when the screen is displayed
    LaunchedEffect(cohortId) {
        viewModel.loadCohortData(cohortId)
    }
    
    val uiState by viewModel.uiState.collectAsState()
    val seatingConfig by viewModel.seatingConfig.collectAsState()
    val teamPositions by viewModel.teamPositions.collectAsState()
    val teamNames by viewModel.teamNames.collectAsState()
    
    var showConfigDialog by remember { mutableStateOf(false) }
    var selectedTeamId by remember { mutableStateOf<UUID?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cohort Seating: ${uiState.cohortName}") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showConfigDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Configure Seating")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }
                
                uiState.error != null -> {
                    ErrorMessage(
                        message = uiState.error!!,
                        onRetry = { viewModel.loadCohortData(cohortId) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                seatingConfig == null -> {
                    NoSeatingConfigView(
                        onConfigureClick = { showConfigDialog = true },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                else -> {
                    // Main content with seating map
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Seating map
                        CohortSeatingMap(
                            seatingConfig = seatingConfig!!,
                            teamPositions = teamPositions,
                            teamNames = teamNames,
                            selectedTeamId = selectedTeamId,
                            onPositionSelected = { row, col, teamId ->
                                // If a team is already selected, try to assign it to this position
                                if (selectedTeamId != null && teamId == null) {
                                    viewModel.assignTeamToPosition(selectedTeamId!!, row, col)
                                    selectedTeamId = null
                                } else {
                                    // Otherwise, select the team at this position if there is one
                                    selectedTeamId = teamId
                                }
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Controls for seating strategies
                        SeatingStrategyControls(
                            currentStrategy = seatingConfig!!.assignmentStrategy,
                            onStrategyChange = { viewModel.applySeatingStrategy(it) }
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Team selection area
                        TeamSelectionArea(
                            teams = uiState.teams,
                            selectedTeamId = selectedTeamId,
                            onTeamSelected = { teamId -> 
                                selectedTeamId = teamId
                            },
                            onTeamDeselected = {
                                selectedTeamId = null
                            },
                            onTeamClick = onTeamClick,
                            onClearPosition = { teamId ->
                                viewModel.clearTeamPosition(teamId)
                                if (selectedTeamId == teamId) {
                                    selectedTeamId = null
                                }
                            }
                        )
                    }
                }
            }
            
            // Seating configuration dialog
            if (showConfigDialog) {
                SeatingConfigDialog(
                    initialConfig = seatingConfig,
                    onDismiss = { showConfigDialog = false },
                    onConfirm = { rows, columns, layoutType, strategy ->
                        viewModel.configureSeating(rows, columns, layoutType, strategy)
                        showConfigDialog = false
                    }
                )
            }
        }
    }
}

@Composable
private fun NoSeatingConfigView(
    onConfigureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No seating configuration",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "This cohort doesn't have a seating arrangement configured yet.",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onConfigureClick) {
            Text("Configure Seating")
        }
    }
}

@Composable
private fun TeamSelectionArea(
    teams: List<Team>,
    selectedTeamId: UUID?,
    onTeamSelected: (UUID) -> Unit,
    onTeamDeselected: () -> Unit,
    onTeamClick: (UUID) -> Unit,
    onClearPosition: (UUID) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Teams",
            style = MaterialTheme.typography.titleLarge
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (teams.isEmpty()) {
                    Text("No teams in this cohort")
                } else {
                    if (selectedTeamId != null) {
                        Text(
                            text = "Selected team: ${teams.find { it.id == selectedTeamId }?.name ?: "Unknown"}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(onClick = onTeamDeselected) {
                                Text("Cancel Selection")
                            }
                            
                            val teamHasPosition = teams.find { it.id == selectedTeamId }?.position != null
                            if (teamHasPosition) {
                                Button(
                                    onClick = { onClearPosition(selectedTeamId) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Clear Position")
                                }
                            }
                            
                            OutlinedButton(onClick = { onTeamClick(selectedTeamId) }) {
                                Text("View Team Details")
                            }
                        }
                    } else {
                        Text(
                            text = "Select a team to assign it to a position, or click on the seating map to select a position.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Team list
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            teams.forEach { team ->
                                TeamListItem(
                                    team = team,
                                    hasPosition = team.position != null,
                                    onSelected = { onTeamSelected(team.id) },
                                    onPositionCleared = { onClearPosition(team.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamListItem(
    team: Team,
    hasPosition: Boolean,
    onSelected: () -> Unit,
    onPositionCleared: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    
    // Apply elevation and scale animations on hover
    val elevation by animateDpAsState(
        targetValue = if (isHovered) 4.dp else 1.dp,
        animationSpec = tween(durationMillis = 150)
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.02f else 1.0f,
        animationSpec = tween(durationMillis = 150)
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .withHoverState { isHovered = it },
        colors = CardDefaults.cardColors(
            containerColor = if (hasPosition) {
                if (isHovered) 
                    MaterialTheme.colorScheme.primaryContainer
                else 
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                if (isHovered)
                    MaterialTheme.colorScheme.surfaceVariant
                else
                    MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = team.name,
                    style = MaterialTheme.typography.titleMedium
                )
                
                if (hasPosition) {
                    Text(
                        text = "Has assigned position",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    // Show additional position info on hover
                    if (isHovered && team.position != null) {
                        Text(
                            text = "Position: Row ${team.position.row + 1}, Column ${team.position.column + 1}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    }
                } else {
                    Text(
                        text = "No position assigned",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Row {
                if (hasPosition) {
                    // Clear button with hover effect
                    var isClearHovered by remember { mutableStateOf(false) }
                    
                    TextButton(
                        onClick = onPositionCleared,
                        modifier = Modifier.withHoverState { isClearHovered = it },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (isClearHovered) 
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        )
                    ) {
                        Text("Clear")
                    }
                }
                
                // Select button with hover effect
                var isSelectHovered by remember { mutableStateOf(false) }
                
                Button(
                    onClick = onSelected,
                    modifier = Modifier.withHoverState { isSelectHovered = it },
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = if (isSelectHovered) 4.dp else 2.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Text("Select")
                }
            }
        }
    }
}

@Composable
private fun SeatingStrategyControls(
    currentStrategy: String,
    onStrategyChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Seating Assignment Strategy",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Choose a strategy to automatically assign teams to seats:",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StrategyButton(
                    text = "FIFO",
                    description = "First come, first served",
                    isSelected = currentStrategy == "FIFO",
                    onClick = { onStrategyChange("FIFO") },
                    modifier = Modifier.weight(1f)
                )
                
                StrategyButton(
                    text = "Confidence",
                    description = "Based on team performance",
                    isSelected = currentStrategy == "CONFIDENCE_BASED",
                    onClick = { onStrategyChange("CONFIDENCE_BASED") },
                    modifier = Modifier.weight(1f)
                )
                
                StrategyButton(
                    text = "Random",
                    description = "Random assignment",
                    isSelected = currentStrategy == "RANDOM",
                    onClick = { onStrategyChange("RANDOM") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StrategyButton(
    text: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }
    
    val elevation by animateDpAsState(
        targetValue = if (isHovered) 6.dp else 2.dp,
        animationSpec = tween(durationMillis = 150)
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.03f else 1.0f,
        animationSpec = tween(durationMillis = 150)
    )
    
    OutlinedCard(
        modifier = modifier
            .scale(scale)
            .withHoverState { isHovered = it }
            .clickable(onClick = onClick),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                if (isHovered)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 1.0f)
                else
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
            } else {
                if (isHovered)
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                else
                    MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected || isHovered) FontWeight.SemiBold else FontWeight.Normal
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}