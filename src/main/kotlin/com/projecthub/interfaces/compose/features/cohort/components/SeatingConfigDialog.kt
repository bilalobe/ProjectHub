package com.projecthub.ui.cohort.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.projecthub.base.cohort.domain.value.SeatingConfiguration
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatingConfigDialog(
    initialConfig: SeatingConfiguration?,
    userHasCustomLayoutPermission: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (rows: Int, columns: Int, layoutType: String, assignmentStrategy: String, customLayout: Map<String, List<Int>>?) -> Unit
) {
    // Default values or initial values if config exists
    var rows by remember { mutableStateOf(initialConfig?.rows ?: 5) }
    var columns by remember { mutableStateOf(initialConfig?.columns ?: 6) }
    var selectedLayoutType by remember { mutableStateOf(initialConfig?.layoutType ?: "CLASSROOM") }
    var selectedStrategy by remember { mutableStateOf(initialConfig?.assignmentStrategy ?: "MANUAL") }
    
    // Custom layout editor state
    val isCustomLayout = selectedLayoutType == "CUSTOM"
    var showCustomLayoutEditor by remember { mutableStateOf(false) }
    val customLayout = remember { 
        mutableStateMapOf<String, MutableList<Int>>().apply {
            initialConfig?.customLayout?.forEach { (key, value) ->
                this[key] = value.toMutableList()
            }
        }
    }
    
    // Tabs for standard vs. custom layout
    var selectedTabIndex by remember { mutableStateOf(if (isCustomLayout) 1 else 0) }
    val tabTitles = listOf("Standard Layout", "Custom Layout")
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Configure Seating",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (userHasCustomLayoutPermission) {
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { 
                                    selectedTabIndex = index
                                    if (index == 1) {
                                        selectedLayoutType = "CUSTOM"
                                    } else if (selectedLayoutType == "CUSTOM") {
                                        selectedLayoutType = "CLASSROOM"
                                    }
                                },
                                text = { Text(title) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Show content based on selected tab
                when (selectedTabIndex) {
                    0 -> {
                        // Standard layout configuration
                        StandardLayoutConfig(
                            rows = rows,
                            columns = columns,
                            selectedLayoutType = selectedLayoutType,
                            onRowsChanged = { rows = it },
                            onColumnsChanged = { columns = it },
                            onLayoutTypeChanged = { selectedLayoutType = it }
                        )
                    }
                    1 -> {
                        if (userHasCustomLayoutPermission) {
                            // Custom layout configuration
                            CustomLayoutConfig(
                                rows = rows, 
                                columns = columns,
                                customLayout = customLayout,
                                onShowEditor = { showCustomLayoutEditor = true }
                            )
                        } else {
                            // User doesn't have permission
                            NoPermissionView()
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Assignment Strategy
                Text(
                    text = "Assignment Strategy",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Column(
                    modifier = Modifier.selectableGroup()
                ) {
                    StrategyOption(
                        title = "Manual",
                        description = "Manually assign teams to seats",
                        selected = selectedStrategy == "MANUAL",
                        onClick = { selectedStrategy = "MANUAL" }
                    )
                    
                    StrategyOption(
                        title = "FIFO (First In, First Out)",
                        description = "Assign seats based on team creation order",
                        selected = selectedStrategy == "FIFO",
                        onClick = { selectedStrategy = "FIFO" }
                    )
                    
                    StrategyOption(
                        title = "Confidence-Based",
                        description = "Assign seats based on team performance metrics",
                        selected = selectedStrategy == "CONFIDENCE_BASED",
                        onClick = { selectedStrategy = "CONFIDENCE_BASED" }
                    )
                    
                    StrategyOption(
                        title = "Random",
                        description = "Randomly assign teams to seats",
                        selected = selectedStrategy == "RANDOM",
                        onClick = { selectedStrategy = "RANDOM" }
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Dialog buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = { 
                            val finalCustomLayout = if (selectedLayoutType == "CUSTOM") {
                                customLayout.mapValues { it.value.toList() }
                            } else null
                            
                            onConfirm(rows, columns, selectedLayoutType, selectedStrategy, finalCustomLayout)
                        }
                    ) {
                        Text("Save Configuration")
                    }
                }
            }
        }
    }
    
    // Custom layout editor dialog
    if (showCustomLayoutEditor) {
        CustomLayoutEditorDialog(
            initialRows = rows,
            initialColumns = columns,
            initialCustomLayout = customLayout,
            onDismiss = { showCustomLayoutEditor = false },
            onConfirm = { editedLayout ->
                customLayout.clear()
                customLayout.putAll(editedLayout)
                showCustomLayoutEditor = false
            }
        )
    }
}

@Composable
private fun StandardLayoutConfig(
    rows: Int,
    columns: Int,
    selectedLayoutType: String,
    onRowsChanged: (Int) -> Unit,
    onColumnsChanged: (Int) -> Unit,
    onLayoutTypeChanged: (String) -> Unit
) {
    // Grid Size Configuration
    Text(
        text = "Grid Size",
        style = MaterialTheme.typography.titleMedium
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rows counter
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Rows")
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { if (rows > 1) onRowsChanged(rows - 1) },
                    enabled = rows > 1
                ) {
                    Text("-")
                }
                
                Text(
                    text = rows.toString(),
                    style = MaterialTheme.typography.titleLarge
                )
                
                IconButton(
                    onClick = { if (rows < 20) onRowsChanged(rows + 1) },
                    enabled = rows < 20
                ) {
                    Text("+")
                }
            }
        }
        
        // Columns counter
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Columns")
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { if (columns > 1) onColumnsChanged(columns - 1) },
                    enabled = columns > 1
                ) {
                    Text("-")
                }
                
                Text(
                    text = columns.toString(),
                    style = MaterialTheme.typography.titleLarge
                )
                
                IconButton(
                    onClick = { if (columns < 20) onColumnsChanged(columns + 1) },
                    enabled = columns < 20
                ) {
                    Text("+")
                }
            }
        }
    }
    
    Text(
        text = "Total capacity: ${rows * columns} seats",
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = 8.dp)
    )
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Layout Type Selection
    Text(
        text = "Layout Type",
        style = MaterialTheme.typography.titleMedium
    )
    
    Column(
        modifier = Modifier.selectableGroup()
    ) {
        LayoutTypeOption(
            title = "Classroom",
            description = "Traditional classroom layout with front-facing rows",
            selected = selectedLayoutType == "CLASSROOM",
            onClick = { onLayoutTypeChanged("CLASSROOM") }
        )
        
        LayoutTypeOption(
            title = "Lab",
            description = "Computer lab style with workstations in grid formation",
            selected = selectedLayoutType == "LAB",
            onClick = { onLayoutTypeChanged("LAB") }
        )
        
        LayoutTypeOption(
            title = "Conference",
            description = "Conference-style layout with focus on central area",
            selected = selectedLayoutType == "CONFERENCE",
            onClick = { onLayoutTypeChanged("CONFERENCE") }
        )
    }
}

@Composable
private fun CustomLayoutConfig(
    rows: Int,
    columns: Int,
    customLayout: Map<String, MutableList<Int>>,
    onShowEditor: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Custom Classroom Layout",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Create a custom seating arrangement by specifying which grid positions are available.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Preview of custom layout
        if (customLayout.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Custom Layout Preview",
                        style = MaterialTheme.typography.titleSmall
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Calculate total seats
                    val totalSeats = customLayout.values.sumOf { it.size }
                    Text(
                        text = "Total Capacity: $totalSeats seats",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Simple visualization of the layout
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(columns.toFloat() / rows.toFloat())
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        for (row in 0 until rows) {
                            for (col in 0 until columns) {
                                val isActive = customLayout[row.toString()]?.contains(col) == true
                                Box(
                                    modifier = Modifier
                                        .size(
                                            width = (1f / columns * 100).dp,
                                            height = (1f / rows * 100).dp
                                        )
                                        .offset(
                                            x = (col.toFloat() / columns * 100).dp,
                                            y = (row.toFloat() / rows * 100).dp
                                        )
                                        .padding(1.dp)
                                        .background(
                                            if (isActive) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                        )
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Text(
                text = "No custom layout defined yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onShowEditor) {
            Text(if (customLayout.isEmpty()) "Create Custom Layout" else "Edit Custom Layout")
        }
    }
}

@Composable
private fun NoPermissionView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Clear,
            contentDescription = "No Permission",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "You don't have permission to create custom layouts.",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Please contact an administrator to request this permission.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CustomLayoutEditorDialog(
    initialRows: Int,
    initialColumns: Int,
    initialCustomLayout: Map<String, MutableList<Int>>,
    onDismiss: () -> Unit,
    onConfirm: (Map<String, MutableList<Int>>) -> Unit
) {
    // Create a mutable copy of the layout that we can modify
    val customLayout = remember { 
        mutableStateMapOf<String, MutableList<Int>>().apply {
            initialCustomLayout.forEach { (key, value) ->
                this[key] = value.toMutableList()
            }
        }
    }
    
    val scope = rememberCoroutineScope()
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = "Custom Layout Editor",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Click on cells to toggle their availability",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Grid editor
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Build grid
                        for (row in 0 until initialRows) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                for (col in 0 until initialColumns) {
                                    val isActive = customLayout[row.toString()]?.contains(col) == true
                                    
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp)
                                            .border(1.dp, MaterialTheme.colorScheme.outline)
                                            .background(
                                                if (isActive) MaterialTheme.colorScheme.primaryContainer
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            .clickable {
                                                scope.launch {
                                                    if (isActive) {
                                                        // Remove cell
                                                        customLayout[row.toString()]?.remove(col)
                                                        if (customLayout[row.toString()]?.isEmpty() == true) {
                                                            customLayout.remove(row.toString())
                                                        }
                                                    } else {
                                                        // Add cell
                                                        if (!customLayout.containsKey(row.toString())) {
                                                            customLayout[row.toString()] = mutableListOf()
                                                        }
                                                        customLayout[row.toString()]?.add(col)
                                                    }
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "R${row+1}C${col+1}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isActive) 
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            else 
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Helper buttons for quick selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = {
                            // Clear all
                            customLayout.clear()
                        }
                    ) {
                        Text("Clear All")
                    }
                    
                    OutlinedButton(
                        onClick = {
                            // Select all
                            for (row in 0 until initialRows) {
                                val rowKey = row.toString()
                                customLayout[rowKey] = (0 until initialColumns).toMutableList()
                            }
                        }
                    ) {
                        Text("Select All")
                    }
                    
                    OutlinedButton(
                        onClick = {
                            // Select alternate (checkerboard pattern)
                            customLayout.clear()
                            for (row in 0 until initialRows) {
                                val rowKey = row.toString()
                                customLayout[rowKey] = mutableListOf()
                                
                                for (col in 0 until initialColumns) {
                                    if ((row + col) % 2 == 0) {
                                        customLayout[rowKey]?.add(col)
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Checkerboard")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { onConfirm(customLayout) }
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

@Composable
private fun LayoutTypeOption(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StrategyOption(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}