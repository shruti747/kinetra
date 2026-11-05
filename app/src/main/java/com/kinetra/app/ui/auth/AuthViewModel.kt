package com.kinetra.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetra.app.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
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

    fun updateFullName(name: String) {
        _uiState.update {
            it.copy(fullName = name)
        }
    }

    fun login(onSuccess: () -> Unit) {
        val current = _uiState.value

        if (current.email.isBlank() || current.password.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Enter your email and password.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            val result = repository.login(
                email = current.email.trim(),
                password = current.password
            )

            result
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            fullName = response.user?.name.orEmpty(),
                            errorMessage = null
                        )
                    }
                    onSuccess()
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
}