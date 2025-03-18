package com.projecthub.ui.profile

import com.projecthub.base.user.api.dto.AppUserProfileDTO
import com.projecthub.base.user.api.dto.UpdateUserRequestDTO
import java.io.File

data class UserProfileState(
    val profile: AppUserProfileDTO? = null,
    val isEditing: Boolean = false,
    val isUploading: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface UserProfileIntent {
    object LoadProfile : UserProfileIntent
    object StartEditing : UserProfileIntent
    object CancelEditing : UserProfileIntent
    data class UpdateProfile(val request: UpdateUserRequestDTO) : UserProfileIntent
    data class UploadAvatar(val file: File) : UserProfileIntent
    data class UpdateStatus(val status: String) : UserProfileIntent
}

sealed interface UserProfileEffect {
    data class ShowError(val message: String) : UserProfileEffect
    object ShowSuccessMessage : UserProfileEffect
    object NavigateBack : UserProfileEffect
}