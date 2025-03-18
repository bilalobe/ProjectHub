package com.projecthub.ui.components.whiteboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class WhiteboardViewModel {
    private val _remoteDrawEvents = MutableSharedFlow<DrawEvent>(replay = 100)
    val remoteDrawEvents: SharedFlow<DrawEvent> = _remoteDrawEvents.asSharedFlow()

    // In a real implementation, this would connect to your collaboration backend
    suspend fun handleDrawEvent(event: DrawEvent) {
        // Here you would typically:
        // 1. Send the event to your collaboration backend
        // 2. The backend would broadcast to other clients
        // 3. Other clients would receive the event via remoteDrawEvents
        
        // For now, we'll just emit locally to demonstrate the flow
        _remoteDrawEvents.emit(event)
    }
}

@Composable
fun rememberWhiteboardViewModel(): WhiteboardViewModel {
    return remember { WhiteboardViewModel() }
}