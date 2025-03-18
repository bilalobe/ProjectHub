package com.projecthub.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException

@Composable
fun ErrorBoundary(
    content: @Composable () -> Unit,
    onError: (Throwable) -> Unit = {},
    fallback: @Composable (Throwable, () -> Unit) -> Unit = { error, retry ->
        DefaultErrorFallback(error, retry)
    }
) {
    var error by remember { mutableStateOf<Throwable?>(null) }
    
    DisposableEffect(Unit) {
        onDispose {
            error = null
        }
    }
    
    if (error != null) {
        fallback(error!!) {
            error = null
        }
    } else {
        try {
            content()
        } catch (e: Throwable) {
            if (e !is CancellationException) {
                error = e
                onError(e)
            } else throw e
        }
    }
}

@Composable
private fun DefaultErrorFallback(
    error: Throwable,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = error.message ?: "An unknown error occurred",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Try Again")
        }
    }
}

@Composable
fun rememberErrorBoundaryState(): ErrorBoundaryState {
    return remember { ErrorBoundaryState() }
}

class ErrorBoundaryState {
    var lastError by mutableStateOf<Throwable?>(null)
        private set
    
    fun setError(error: Throwable?) {
        lastError = error
    }
    
    fun clearError() {
        lastError = null
    }
}