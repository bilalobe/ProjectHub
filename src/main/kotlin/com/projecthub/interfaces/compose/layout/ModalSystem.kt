package com.projecthub.ui.layout

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ProjectHubModal(
    visible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
    properties: DialogProperties = DialogProperties()
) {
    if (visible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = properties
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                content()
            }
        }
    }
}

@Composable
fun ConfirmationDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    message: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel"
) {
    ProjectHubModal(
        visible = visible,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 400.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(dismissText)
                }
                
                Button(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(confirmText)
                }
            }
        }
    }
}

@Composable
fun FormDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    content: @Composable ColumnScope.() -> Unit,
    actions: @Composable RowScope.() -> Unit
) {
    ProjectHubModal(
        visible = visible,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 600.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            content()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                actions()
            }
        }
    }
}

@Composable
fun AlertDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    message: String,
    dismissText: String = "OK"
) {
    ProjectHubModal(
        visible = visible,
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 400.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onDismiss) {
                    Text(dismissText)
                }
            }
        }
    }
}