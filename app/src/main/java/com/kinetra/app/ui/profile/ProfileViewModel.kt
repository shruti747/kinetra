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
    val id: Int = 2,
    val name: String = "SHRUTI S",
    val email: String = "252514022.sclas@saveetha.com",
    val phone: String = "+91 98765 43210",
    val bio: String = "Endurance runner & marathon athlete",
    val location: String = "London, UK",
    val sports: String = "Running · Cycling",
    val profilePhoto: String = "",
    val weeklyKm: String = "24.8 km",
    val nextGoal: String = "Break 46 minutes for 10K",
    val personalRecords: List<Pair<String, String>> = listOf(
        "5K" to "18:42 GPS estimate",
        "10K" to "46:12 Official · PB",
        "Half" to "1:38:04 Official · PB"
    ),
    val unreadNotificationsCount: Int = 3,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        val currentCached = UserSession.userProfile.value
        _uiState.update {
            it.copy(
                id = currentCached.id,
                name = currentCached.name,
                email = currentCached.email,
                phone = currentCached.phone.orEmpty(),
                bio = currentCached.bio.orEmpty(),
                location = currentCached.location ?: "London, UK",
                sports = currentCached.preferred_sports ?: "Running · Cycling",
                profilePhoto = currentCached.profile_photo.orEmpty()
            )
        }

        viewModelScope.launch {
            try {
                val response = RetrofitClient.kinetraApi.getProfile(UserSession.currentUserId)
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
                                location = user.location ?: "London, UK",
                                sports = user.preferred_sports ?: "Running · Cycling",
                                profilePhoto = user.profile_photo.orEmpty(),
                                errorMessage = null
                            )
                        }
                    }
                }
            } catch (_: Exception) {
                // Kept local session cache safely
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

            val userId = UserSession.currentUserId
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
                if (response.isSuccessful && response.body()?.success == true) {
                    val updatedUser = response.body()?.user ?: UserProfile(
                        id = userId,
                        name = trimmedName,
                        email = trimmedEmail,
                        phone = phone,
                        bio = bio,
                        location = location,
                        preferred_sports = sports,
                        profile_photo = profilePhotoUri
                    )

                    UserSession.updateProfile(context, updatedUser)

                    _uiState.update {
                        it.copy(
                            id = updatedUser.id,
                            name = updatedUser.name,
                            email = updatedUser.email,
                            phone = updatedUser.phone.orEmpty(),
                            bio = updatedUser.bio.orEmpty(),
                            location = updatedUser.location ?: "London, UK",
                            sports = updatedUser.preferred_sports ?: "Running · Cycling",
                            profilePhoto = updatedUser.profile_photo.orEmpty(),
                            isSaving = false,
                            errorMessage = null,
                            successMessage = "Profile updated successfully!"
                        )
                    }
                    onSuccess()
                } else {
                    val errorMsg = response.body()?.message ?: "Failed to update profile on server."
                    // Fallback to local session update if server unreachable
                    fallbackLocalSave(request, context, errorMsg)
                    onError(errorMsg)
                }
            } catch (e: Exception) {
                // If offline or network issue, save locally so athlete changes are never lost
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
                location = localProfile.location ?: "London, UK",
                sports = localProfile.preferred_sports ?: "Running · Cycling",
                profilePhoto = localProfile.profile_photo.orEmpty(),
                isSaving = false,
                successMessage = message
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
