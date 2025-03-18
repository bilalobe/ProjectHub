package com.projecthub.compose.features.teams

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.projecthub.compose.components.SearchBar
import com.projecthub.compose.components.TeamCard
import com.projecthub.compose.features.teams.TeamsContract.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(
    viewModel: TeamsViewModel,
    onNavigateToTeamDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.stateFlow.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Collect side effects
    LaunchedEffect(viewModel) {
        viewModel.collectEffects { effect ->
            when (effect) {
                is Effect.NavigateToTeamDetails -> {
                    onNavigateToTeamDetails(effect.teamId)
                }
                is Effect.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Long
                    )
                }
                is Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }
    
    // Load initial data
    LaunchedEffect(Unit) {
        viewModel.processIntent(Intent.LoadTeams)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("Teams") },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    actions = {
                        // Sort menu
                        var sortExpanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { sortExpanded = true }) {
                            Icon(Icons.Default.Sort, "Sort Teams")
                        }
                        DropdownMenu(
                            expanded = sortExpanded,
                            onDismissRequest = { sortExpanded = false }
                        ) {
                            TeamSortOrder.values().forEach { order ->
                                DropdownMenuItem(
                                    text = { Text(order.name.replace("_", " ").lowercase().capitalize()) },
                                    leadingIcon = {
                                        if (state.sortOrder == order) {
                                            Icon(Icons.Default.Check, null)
                                        }
                                    },
                                    onClick = {
                                        viewModel.processIntent(Intent.SortTeams(order))
                                        sortExpanded = false
                                    }
                                )
                            }
                        }
                        
                        // Filter menu
                        var filterExpanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { filterExpanded = true }) {
                            Icon(Icons.Default.FilterList, "Filter Teams")
                        }
                        DropdownMenu(
                            expanded = filterExpanded,
                            onDismissRequest = { filterExpanded = false }
                        ) {
                            TeamFilter.values().forEach { filter ->
                                DropdownMenuItem(
                                    text = { Text(filter.name.replace("_", " ").lowercase().capitalize()) },
                                    leadingIcon = {
                                        if (state.selectedFilter == filter) {
                                            Icon(Icons.Default.Check, null)
                                        }
                                    },
                                    onClick = {
                                        viewModel.processIntent(Intent.FilterTeams(filter))
                                        filterExpanded = false
                                    }
                                )
                            }
                        }
                    }
                )

                // Search bar
                SearchBar(
                    onSearch = { query ->
                        viewModel.processIntent(Intent.SearchTeams(query))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = "Search teams..."
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add new team intent */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.GroupAdd, "New Team")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading && state.teams.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 300.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = state.teams,
                        key = { it.id }
                    ) { team ->
                        TeamCard(
                            team = team,
                            onClick = {
                                viewModel.processIntent(Intent.SelectTeam(team.id))
                            },
                            onStatusChange = { newStatus ->
                                viewModel.processIntent(Intent.UpdateTeamStatus(team.id, newStatus))
                            }
                        )
                    }
                    
                    // Empty state
                    if (state.teams.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (state.error != null)
                                        "An error occurred. Pull to refresh."
                                    else
                                        "No teams found",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }

            // Loading indicator for refresh
            if (state.isLoading && state.teams.isNotEmpty()) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }
    }
}