package com.projecthub.ui.profile

import com.projecthub.base.user.application.profile.service.AppUserProfileService
import com.projecthub.base.user.application.profile.service.AppUserAvatarService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class UserProfileViewModel(
    private val profileService: AppUserProfileService,
    private val avatarService: AppUserAvatarService,
    private val currentUserId: UUID
) {
    private val _state = MutableStateFlow(UserProfileState())
    val state: StateFlow<UserProfileState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<UserProfileEffect>()
    val effects = _effects.asSharedFlow()

    init {
        handleIntent(UserProfileIntent.LoadProfile)
    }

    fun handleIntent(intent: UserProfileIntent) {
        when (intent) {
            is UserProfileIntent.LoadProfile -> loadProfile()
            is UserProfileIntent.StartEditing -> startEditing()
            is UserProfileIntent.CancelEditing -> cancelEditing()
            is UserProfileIntent.UpdateProfile -> updateProfile(intent.request)
            is UserProfileIntent.UploadAvatar -> uploadAvatar(intent.file)
            is UserProfileIntent.UpdateStatus -> updateStatus(intent.status)
        }
    }

    private fun loadProfile() {
        _state.value = _state.value.copy(isLoading = true)
        try {
            val profile = profileService.getUserProfileById(currentUserId)
            _state.value = _state.value.copy(
                profile = profile,
                isLoading = false
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                error = e.message,
                isLoading = false
            )
        }
    }

    private fun startEditing() {
        _state.value = _state.value.copy(isEditing = true)
    }

    private fun cancelEditing() {
        _state.value = _state.value.copy(isEditing = false)
    }

    private fun updateProfile(request: UpdateUserRequestDTO) {
        try {
            val updatedProfile = profileService.updateUserProfile(currentUserId, request)
            _state.value = _state.value.copy(
                profile = updatedProfile,
                isEditing = false
            )
            _effects.tryEmit(UserProfileEffect.ShowSuccessMessage)
        } catch (e: Exception) {
            _effects.tryEmit(UserProfileEffect.ShowError(e.message ?: "Error updating profile"))
        }
    }

    private fun uploadAvatar(file: File) {
        _state.value = _state.value.copy(isUploading = true)
        try {
            avatarService.updateAvatar(currentUserId, file.toMultipartFile())
            loadProfile() // Reload to get updated avatar URL
            _effects.tryEmit(UserProfileEffect.ShowSuccessMessage)
        } catch (e: Exception) {
            _effects.tryEmit(UserProfileEffect.ShowError(e.message ?: "Error uploading avatar"))
        } finally {
            _state.value = _state.value.copy(isUploading = false)
        }
    }

    private fun updateStatus(status: String) {
        try {
            profileService.updateStatus(currentUserId, status)
            loadProfile() // Reload to get updated status
            _effects.tryEmit(UserProfileEffect.ShowSuccessMessage)
        } catch (e: Exception) {
            _effects.tryEmit(UserProfileEffect.ShowError(e.message ?: "Error updating status"))
        }
    }
}

private fun File.toMultipartFile(): MultipartFile {
    return object : MultipartFile {
        override fun getName() = name
        override fun getOriginalFilename() = name
        override fun getContentType() = "image/*"
        override fun isEmpty() = length() == 0L
        override fun getSize() = length()
        override fun getBytes() = readBytes()
        override fun getInputStream() = inputStream()
        override fun transferTo(dest: File) = copyTo(dest)
    }
}