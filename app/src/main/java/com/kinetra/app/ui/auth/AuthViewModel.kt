package com.kinetra.app.ui.auth

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetra.app.data.AuthRepository
import com.kinetra.app.data.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullName: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(email = email, errorMessage = null)
        }
    }

    fun updatePassword(password: String) {
        _uiState.update {
            it.copy(password = password, errorMessage = null)
        }
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update {
            it.copy(confirmPassword = confirmPassword, errorMessage = null)
        }
    }

    fun updateFullName(name: String) {
        _uiState.update {
            it.copy(fullName = name, errorMessage = null)
        }
    }

    fun clearForm() {
        _uiState.value = AuthUiState()
    }

    fun login(context: Context, onSuccess: () -> Unit) {
        val current = _uiState.value
        val trimmedEmail = current.email.trim()

        if (trimmedEmail.isBlank() || current.password.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Enter your email and password.")
            }
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _uiState.update {
                it.copy(errorMessage = "Enter a valid email address.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            val result = repository.login(
                email = trimmedEmail,
                password = current.password,
                deviceLabel = SessionManager.getDeviceLabel(context)
            )

            result
                .onSuccess { response ->
                    val user = response.user
                    val token = response.token
                    if (user != null && !token.isNullOrBlank()) {
                        SessionManager.saveAuthenticatedSession(
                            context = context,
                            user = user.toUserProfile(),
                            token = token,
                            expiresAt = response.expires_at
                        )
                        // Never keep plain-text password in memory after login
                        _uiState.value = AuthUiState(fullName = user.name)
                        onSuccess()
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Server returned an incomplete login session."
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Login failed."
                        )
                    }
                }
        }
    }

    fun register(context: Context, onSuccess: () -> Unit) {
        val current = _uiState.value
        val trimmedName = current.fullName.trim()
        val trimmedEmail = current.email.trim()

        when {
            trimmedName.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Enter your full name.") }
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> {
                _uiState.update { it.copy(errorMessage = "Enter a valid email address.") }
                return
            }
            current.password.length < 8 -> {
                _uiState.update { it.copy(errorMessage = "Password must be at least 8 characters.") }
                return
            }
            current.password != current.confirmPassword -> {
                _uiState.update { it.copy(errorMessage = "Passwords do not match.") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            val result = repository.register(
                name = trimmedName,
                email = trimmedEmail,
                password = current.password,
                deviceLabel = SessionManager.getDeviceLabel(context)
            )

            result
                .onSuccess { response ->
                    val user = response.user
                    val token = response.token
                    if (user != null && !token.isNullOrBlank()) {
                        SessionManager.saveAuthenticatedSession(
                            context = context,
                            user = user.toUserProfile(),
                            token = token,
                            expiresAt = response.expires_at
                        )
                        _uiState.value = AuthUiState(fullName = user.name)
                        onSuccess()
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Registration succeeded, but session token was missing."
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Registration failed."
                        )
                    }
                }
        }
    }

    fun logout(context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {
            // Revoke ONLY this device's token on backend before wiping local storage
            repository.logoutFromServer()
            SessionManager.clearSession(context)
            _uiState.value = AuthUiState()
            onComplete()
        }
    }
}
