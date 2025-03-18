package com.projecthub.ui.components.whiteboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@Composable
fun WhiteboardScreen() {
    val viewModel = rememberWhiteboardViewModel()
    val scope = rememberCoroutineScope()
    
    Surface(modifier = Modifier.fillMaxSize()) {
        CollaborativeWhiteboard(
            onDrawEvent = { drawEvent ->
                scope.launch {
                    viewModel.handleDrawEvent(drawEvent)
                }
            },
            remoteDrawEvents = viewModel.remoteDrawEvents
        )
    }
}