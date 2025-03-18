package com.projecthub.ui.cohort.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.projecthub.base.cohort.domain.value.SeatingConfiguration
import com.projecthub.base.team.domain.entity.Team
import com.projecthub.base.team.domain.value.TeamPosition
import com.projecthub.ui.shared.hover.HoverEffect.withConditionalHover
import java.util.*

/**
 * A composable that displays a seating map for a cohort.
 * 
 * @param seatingConfig The seating configuration for the cohort
 * @param teamPositions Map of teamId to team position
 * @param teamNames Map of teamId to team name
 * @param onPositionSelected Callback when a seat position is selected
 * @param selectedTeamId Optional currently selected team ID
 * @param modifier Modifier for styling
 */
@Composable
fun CohortSeatingMap(
    seatingConfig: SeatingConfiguration,
    teamPositions: Map<UUID, TeamPosition>,
    teamNames: Map<UUID, String>,
    onPositionSelected: (row: Int, column: Int, teamId: UUID?) -> Unit,
    selectedTeamId: UUID? = null,
    modifier: Modifier = Modifier
) {
    var selectedPosition by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    
    Column(modifier = modifier.fillMaxWidth()) {
        // Layout header with information
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Seating Arrangement",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Text(
                text = "Layout: ${seatingConfig.layoutType}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        // Strategy badge
        StrategySelectorChip(seatingConfig.assignmentStrategy)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LegendItem(
                color = MaterialTheme.colorScheme.primaryContainer,
                text = "Occupied"
            )
            LegendItem(
                color = MaterialTheme.colorScheme.surfaceVariant,
                text = "Available"
            )
            LegendItem(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                text = "Selected"
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Orientation indicators
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            // Center "FRONT" text
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "FRONT",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        
        // Seating grid
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(seatingConfig.columns.toFloat() / seatingConfig.rows.toFloat())
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            // Draw grid layout
            GridLayout(seatingConfig, teamPositions, teamNames, selectedTeamId) { row, col, teamId -> 
                selectedPosition = row to col
                onPositionSelected(row, col, teamId)
            }
            
            // Draw front desk or podium
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.6f)
                    .height(24.dp)
                    .offset(y = (-12).dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "Instructor Area",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            // Center "BACK" text
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "BACK",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        
        selectedPosition?.let { (row, col) -> 
            val teamId = teamPositions.entries.find { 
                it.value.row == row && it.value.column == col 
            }?.key
            
            AnimatedVisibility(visible = true) {
                SelectedPositionInfo(
                    row = row,
                    column = col,
                    teamId = teamId,
                    teamName = teamId?.let { teamNames[it] ?: "Unknown Team" } ?: "Empty Seat"
                )
            }
        }
    }
}

@Composable
private fun GridLayout(
    seatingConfig: SeatingConfiguration,
    teamPositions: Map<UUID, TeamPosition>,
    teamNames: Map<UUID, String>,
    selectedTeamId: UUID?,
    onPositionSelected: (Int, Int, UUID?) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(seatingConfig.columns),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val seats = List(seatingConfig.rows * seatingConfig.columns) { index -> 
            val row = index / seatingConfig.columns
            val col = index % seatingConfig.columns
            
            val teamEntry = teamPositions.entries.find { 
                it.value.row == row && it.value.column == col
            }
            
            SeatInfo(
                row = row,
                column = col,
                teamId = teamEntry?.key,
                teamName = teamEntry?.key?.let { teamNames[it] ?: "Unknown" }
            )
        }
        
        items(seats) { seat -> 
            SeatBox(
                seatInfo = seat,
                isSelected = selectedTeamId != null && seat.teamId == selectedTeamId,
                onClick = { onPositionSelected(seat.row, seat.column, seat.teamId) }
            )
        }
    }
}

@Composable
private fun SeatBox(
    seatInfo: SeatInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isOccupied = seatInfo.teamId != null
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else if (isOccupied) 4.dp else 1.dp,
        animationSpec = tween(durationMillis = 200)
    )
    
    var isHovered by remember { mutableStateOf(false) }
    
    // Scale animation on hover
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.05f else 1.0f,
        animationSpec = tween(durationMillis = 150)
    )
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .clip(RoundedCornerShape(4.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .withConditionalHover(
                onHovered = {
                    isHovered = true
                    border(
                        width = if (isSelected) 2.dp else 1.5.dp,
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(4.dp)
                    )
                },
                onNotHovered = {
                    isHovered = false
                    border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(4.dp)
                    )
                }
            )
            .padding(4.dp)
            .shadow(
                elevation = if (isHovered) elevation + 2.dp else elevation,
                shape = RoundedCornerShape(4.dp)
            )
            .background(
                color = when {
                    isSelected -> MaterialTheme.colorScheme.tertiaryContainer
                    isOccupied -> MaterialTheme.colorScheme.primaryContainer
                    isHovered -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isOccupied) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = seatInfo.teamName ?: "?",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = if (isHovered) 13.sp else 12.sp
                    ),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(2.dp)
                )
                
                // Show row/column info on hover
                if (isHovered) {
                    Text(
                        text = "R${seatInfo.row + 1}C${seatInfo.column + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            Text(
                text = "R${seatInfo.row + 1}C${seatInfo.column + 1}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = if (isHovered) 0.9f else 0.7f
                )
            )
        }
        
        // Tooltip-like effect on hover (for desktop/web)
        if (isHovered) {
            HoverTooltip(
                seatInfo = seatInfo,
                isOccupied = isOccupied
            )
        }
    }
}

@Composable
private fun BoxScope.HoverTooltip(
    seatInfo: SeatInfo,
    isOccupied: Boolean
) {
    Surface(
        modifier = Modifier
            .width(120.dp)
            .align(Alignment.BottomEnd)
            .offset(x = 10.dp, y = 10.dp),
        tonalElevation = 8.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Position: R${seatInfo.row + 1}, C${seatInfo.column + 1}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = if (isOccupied) "Occupied" else "Available",
                style = MaterialTheme.typography.labelSmall,
                color = if (isOccupied) 
                    MaterialTheme.colorScheme.primary
                else 
                    MaterialTheme.colorScheme.secondary
            )
            
            if (isOccupied && seatInfo.teamName != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Team: ${seatInfo.teamName}",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SelectedPositionInfo(
    row: Int,
    column: Int,
    teamId: UUID?,
    teamName: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Selected Position:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Row ${row+1}, Column ${column+1}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Status:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if (teamId != null) "Occupied" else "Available",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (teamId != null) 
                        MaterialTheme.colorScheme.primary
                    else 
                        MaterialTheme.colorScheme.secondary
                )
            }
            
            if (teamId != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Team:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = teamName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun StrategySelectorChip(
    currentStrategy: String
) {
    AssistChip(
        onClick = { /* Strategy change would be handled elsewhere */ },
        label = { Text("Strategy: $currentStrategy") },
        leadingIcon = {
            when (currentStrategy) {
                "FIFO" -> Icon(Icons.Default.Schedule, "FIFO Strategy")
                "CONFIDENCE_BASED" -> Icon(Icons.Default.Star, "Confidence Strategy")
                "RANDOM" -> Icon(Icons.Default.Shuffle, "Random Strategy")
                else -> Icon(Icons.Default.Edit, "Manual Strategy")
            }
        }
    )
}

data class SeatInfo(
    val row: Int,
    val column: Int,
    val teamId: UUID? = null,
    val teamName: String? = null
)

@Preview
@Composable
private fun CohortSeatingMapPreview() {
    MaterialTheme {
        val mockConfig = SeatingConfiguration(5, 6, "CLASSROOM", "MANUAL")
        val mockTeams = mapOf(
            UUID.randomUUID() to TeamPosition(0, 1, 75, System.currentTimeMillis()),
            UUID.randomUUID() to TeamPosition(1, 2, 60, System.currentTimeMillis()),
            UUID.randomUUID() to TeamPosition(2, 3, 45, System.currentTimeMillis()),
            UUID.randomUUID() to TeamPosition(3, 4, 30, System.currentTimeMillis())
        )
        val mockNames = mockTeams.mapValues { "Team ${it.key.toString().substring(0, 4)}" }

        CohortSeatingMap(
            seatingConfig = mockConfig,
            teamPositions = mockTeams,
            teamNames = mockNames,
            onPositionSelected = { _, _, _ -> }
        )
    }
}