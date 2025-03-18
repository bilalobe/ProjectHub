package com.projecthub.ui.login.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.projecthub.ui.components.Loading
import com.projecthub.ui.login.model.LoginEvent
import com.projecthub.ui.login.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val state = viewModel.state
    
    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onLoginSuccess()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                Loading()
            } else {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .widthIn(max = 400.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "ProjectHub",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Sign In",
                                style = MaterialTheme.typography.titleLarge
                            )
                            
                            OutlinedTextField(
                                value = state.credentials.username,
                                onValueChange = { username ->
                                    viewModel.emitEvent(LoginEvent.UpdateUsername(username))
                                },
                                label = { Text("Username") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null)
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            OutlinedTextField(
                                value = state.credentials.password,
                                onValueChange = { password ->
                                    viewModel.emitEvent(LoginEvent.UpdatePassword(password))
                                },
                                label = { Text("Password") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null)
                                },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            if (state.error != null) {
                                Text(
                                    text = state.error,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            
                            Button(
                                onClick = {
                                    viewModel.emitEvent(LoginEvent.SubmitLogin)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Sign In")
                            }
                        }
                    }
                }
            }
        }
    }
}