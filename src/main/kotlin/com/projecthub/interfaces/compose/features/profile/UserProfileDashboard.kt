package com.projecthub.ui.profile

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.projecthub.ui.components.*
import com.projecthub.ui.theme.ProjectHubTheme

@Composable
fun UserProfileDashboard(
    viewModel: UserProfileViewModel,
    modifier: Modifier = Modifier
) {
    ProjectHubTheme {
        val state by viewModel.state.collectAsState()

        LoadingOverlay(isLoading = state.isLoading) {
            PageContainer(
                title = "Profile",
                modifier = modifier
            ) {
                state.profile?.let { profile ->
                    ContentCard(
                        title = "Profile Information",
                        titleActions = {
                            if (!state.isEditing) {
                                IconButton(
                                    icon = Icons.Default.Edit,
                                    onClick = {
                                        viewModel.handleIntent(UserProfileIntent.StartEditing)
                                    },
                                    contentDescription = "Edit profile"
                                )
                            }
                        }
                    ) {
                        if (state.isEditing) {
                            ProfileEditForm(
                                profile = profile,
                                onUpdateProfile = { request ->
                                    viewModel.handleIntent(UserProfileIntent.UpdateProfile(request))
                                },
                                onCancel = {
                                    viewModel.handleIntent(UserProfileIntent.CancelEditing)
                                }
                            )
                        } else {
                            ProfileDisplay(profile = profile)
                        }
                    }

                    GridContainer(columns = 3) {
                        StatisticCard(
                            title = "Posts",
                            value = profile.postCount.toString(),
                            color = MaterialTheme.colorScheme.primary
                        )
                        StatisticCard(
                            title = "Followers",
                            value = profile.followerCount.toString(),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        StatisticCard(
                            title = "Following",
                            value = profile.followingCount.toString(),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    ContentCard(
                        title = "Status",
                        titleActions = {
                            IconButton(
                                icon = Icons.Default.Edit,
                                onClick = { /* Show status edit dialog */ },
                                contentDescription = "Edit status"
                            )
                        }
                    ) {
                        StatusSection(
                            currentStatus = profile.statusMessage,
                            onUpdateStatus = { status ->
                                viewModel.handleIntent(UserProfileIntent.UpdateStatus(status))
                            }
                        )
                    }
                }

                state.error?.let { error ->
                    StatusSnackbar(
                        message = error,
                        isError = true
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileDisplay(
    profile: AppUserProfileDTO
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.size(120.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    KamelImage(
                        resource = asyncPainterResource(profile.avatarUrl),
                        contentDescription = "Profile avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Column {
                Text(
                    text = "${profile.firstName} ${profile.lastName}",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "@${profile.username}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = profile.email,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ProfileEditForm(
    profile: AppUserProfileDTO,
    onUpdateProfile: (UpdateUserRequestDTO) -> Unit,
    onCancel: () -> Unit
) {
    var firstName by remember { mutableStateOf(profile.firstName) }
    var lastName by remember { mutableStateOf(profile.lastName) }
    var email by remember { mutableStateOf(profile.email) }

    FormSection {
        ValidatedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = "First Name"
        )
        ValidatedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = "Last Name"
        )
        ValidatedTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email"
        )

        Row(
            horizontalArrangement = Arrangement.End
        ) {
            SecondaryButton(
                text = "Cancel",
                onClick = onCancel
            )
            PrimaryButton(
                text = "Save",
                onClick = {
                    onUpdateProfile(
                        UpdateUserRequestDTO(
                            firstName = firstName,
                            lastName = lastName,
                            email = email
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun StatusSection(
    currentStatus: String,
    onUpdateStatus: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf(currentStatus) }

    Column {
        if (isEditing) {
            ValidatedTextField(
                value = status,
                onValueChange = { status = it },
                label = "What's on your mind?",
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.End
            ) {
                SecondaryButton(
                    text = "Cancel",
                    onClick = { isEditing = false }
                )
                PrimaryButton(
                    text = "Save",
                    onClick = {
                        onUpdateStatus(status)
                        isEditing = false
                    }
                )
            }
        } else {
            Text(
                text = currentStatus,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.clickable { isEditing = true }
            )
        }
    }
}