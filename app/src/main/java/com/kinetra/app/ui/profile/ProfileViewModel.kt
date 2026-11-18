package com.kinetra.app.ui.profile

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetra.app.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val id: Int = -1,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val bio: String = "",
    val location: String = "",
    val sports: String = "",
    val profilePhoto: String = "",
    val weeklyKm: String = "0.0 km",
    val nextGoal: String = "Set your weekly target",
    val personalRecords: List<Pair<String, String>> = listOf(
        "5K" to "No record yet",
        "10K" to "No record yet",
        "Half" to "No record yet"
    ),
    val unreadNotificationsCount: Int = 0,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile() {
        if (!SessionManager.isAuthenticated()) {
            clearUserData()
            return
        }

        val currentCached = UserSession.userProfile.value
        if (currentCached.id > 0) {
            _uiState.update {
                it.copy(
                    id = currentCached.id,
                    name = currentCached.name,
                    email = currentCached.email,
                    phone = currentCached.phone.orEmpty(),
                    bio = currentCached.bio.orEmpty(),
                    location = currentCached.location.orEmpty(),
                    sports = currentCached.preferred_sports.orEmpty(),
                    profilePhoto = currentCached.profile_photo.orEmpty(),
                    errorMessage = null
                )
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val userId = SessionManager.getCurrentUserId()
                if (userId <= 0) {
                    clearUserData()
                    return@launch
                }

                val response = RetrofitClient.kinetraApi.getProfile(userId)
                if (response.code() == 401 || response.code() == 403) {
                    SessionManager.clearSession()
                    clearUserData()
                    return@launch
                }

                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.user
                    if (user != null) {
                        UserSession.updateProfile(null, user)
                        _uiState.update {
                            it.copy(
                                id = user.id,
                                name = user.name,
                                email = user.email,
                                phone = user.phone.orEmpty(),
                                bio = user.bio.orEmpty(),
                                location = user.location.orEmpty(),
                                sports = user.preferred_sports.orEmpty(),
                                profilePhoto = user.profile_photo.orEmpty(),
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                        return@launch
                    }
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (_: Exception) {
                // Keep authenticated user's local device cache safely when offline
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateProfile(
        name: String,
        email: String,
        phone: String,
        bio: String,
        location: String,
        sports: String,
        profilePhotoUri: String,
        context: Context?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = SessionManager.getCurrentUserId()
        if (userId <= 0 || !SessionManager.isAuthenticated()) {
            onError("Your session has expired. Please sign in again.")
            return
        }

        val trimmedName = name.trim()
        val trimmedEmail = email.trim()

        if (trimmedName.isEmpty()) {
            val error = "Athlete name cannot be empty."
            _uiState.update { it.copy(errorMessage = error) }
            onError(error)
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            val error = "Please enter a valid email address."
            _uiState.update { it.copy(errorMessage = error) }
            onError(error)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }

            val request = ProfileUpdateRequest(
                user_id = userId,
                name = trimmedName,
                email = trimmedEmail,
                phone = phone.trim(),
                bio = bio.trim(),
                location = location.trim(),
                preferred_sports = sports.trim(),
                profile_photo = profilePhotoUri.trim()
            )

            try {
                val response = RetrofitClient.kinetraApi.updateProfile(request)
                if (response.code() == 401 || response.code() == 403) {
                    SessionManager.clearSession(context)
                    clearUserData()
                    onError("Session expired. Please sign in again.")
                    return@launch
                }

                if (response.isSuccessful && response.body()?.success == true) {
                    val updatedUser = response.body()?.user ?: UserProfile(
                        id = userId,
                        name = trimmedName,
                        email = trimmedEmail,
                        phone = phone.trim(),
                        bio = bio.trim(),
                        location = location.trim(),
                        preferred_sports = sports.trim(),
                        profile_photo = profilePhotoUri.trim()
                    )

                    UserSession.updateProfile(context, updatedUser)

                    _uiState.update {
                        it.copy(
                            id = updatedUser.id,
                            name = updatedUser.name,
                            email = updatedUser.email,
                            phone = updatedUser.phone.orEmpty(),
                            bio = updatedUser.bio.orEmpty(),
                            location = updatedUser.location.orEmpty(),
                            sports = updatedUser.preferred_sports.orEmpty(),
                            profilePhoto = updatedUser.profile_photo.orEmpty(),
                            isSaving = false,
                            errorMessage = null,
                            successMessage = "Profile updated successfully!"
                        )
                    }
                    onSuccess()
                } else {
                    val errorMsg = response.body()?.message ?: "Failed to update profile on server."
                    _uiState.update { it.copy(isSaving = false, errorMessage = errorMsg) }
                    onError(errorMsg)
                }
            } catch (_: Exception) {
                // Save locally for this authenticated user if device is temporarily offline
                fallbackLocalSave(request, context, "Saved locally (Network unavailable).")
                onSuccess()
            }
        }
    }

    private fun fallbackLocalSave(request: ProfileUpdateRequest, context: Context?, message: String) {
        val localProfile = UserProfile(
            id = request.user_id,
            name = request.name,
            email = request.email,
            phone = request.phone,
            bio = request.bio,
            location = request.location,
            preferred_sports = request.preferred_sports,
            profile_photo = request.profile_photo
        )
        UserSession.updateProfile(context, localProfile)

        _uiState.update {
            it.copy(
                id = localProfile.id,
                name = localProfile.name,
                email = localProfile.email,
                phone = localProfile.phone.orEmpty(),
                bio = localProfile.bio.orEmpty(),
                location = localProfile.location.orEmpty(),
                sports = localProfile.preferred_sports.orEmpty(),
                profilePhoto = localProfile.profile_photo.orEmpty(),
                isSaving = false,
                successMessage = message
            )
        }
    }

    fun clearUserData() {
        _uiState.value = ProfileUiState()
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
