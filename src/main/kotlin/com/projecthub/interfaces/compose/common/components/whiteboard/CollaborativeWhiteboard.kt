package com.projecthub.ui.components.whiteboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

data class DrawEvent(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun CollaborativeWhiteboard(
    modifier: Modifier = Modifier,
    onDrawEvent: (DrawEvent) -> Unit = {},
    remoteDrawEvents: SharedFlow<DrawEvent>
) {
    var currentPath by remember { mutableStateOf(mutableListOf<Offset>()) }
    val paths = remember { mutableStateListOf<DrawEvent>() }
    var currentColor by remember { mutableStateOf(Color.Black) }
    var currentStrokeWidth by remember { mutableStateOf(2f) }
    
    LaunchedEffect(Unit) {
        remoteDrawEvents.collect { drawEvent ->
            paths.add(drawEvent)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Toolbar
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Color picker
            Row {
                listOf(Color.Black, Color.Red, Color.Blue, Color.Green).forEach { color ->
                    IconButton(
                        onClick = { currentColor = color },
                        modifier = Modifier.size(32.dp).background(color).padding(4.dp)
                    ) {}
                }
            }
            
            // Stroke width slider
            Slider(
                value = currentStrokeWidth,
                onValueChange = { currentStrokeWidth = it },
                valueRange = 1f..20f,
                modifier = Modifier.width(200.dp)
            )
            
            // Clear button
            Button(onClick = { paths.clear() }) {
                Text("Clear")
            }
        }
        
        // Drawing canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { currentPath = mutableListOf() },
                        onDrag = { change, _ ->
                            currentPath.add(change.position)
                            paths.add(DrawEvent(
                                points = currentPath.toList(),
                                color = currentColor,
                                strokeWidth = currentStrokeWidth
                            ))
                            onDrawEvent(DrawEvent(
                                points = currentPath.toList(),
                                color = currentColor,
                                strokeWidth = currentStrokeWidth
                            ))
                        }
                    )
                }
        ) {
            // Draw all paths
            paths.forEach { drawEvent ->
                if (drawEvent.points.size > 1) {
                    drawPath(
                        path = Path().apply {
                            moveTo(drawEvent.points.first().x, drawEvent.points.first().y)
                            drawEvent.points.forEach { point ->
                                lineTo(point.x, point.y)
                            }
                        },
                        color = drawEvent.color,
                        style = Stroke(width = drawEvent.strokeWidth)
                    )
                }
            }
        }
    }
}