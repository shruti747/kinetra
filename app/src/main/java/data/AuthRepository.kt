package com.kinetra.app.data

import com.google.gson.Gson
import java.io.IOException

sealed class SessionValidationResult {
    data class Valid(val user: UserProfile) : SessionValidationResult()
    object InvalidOrExpired : SessionValidationResult()
    object NetworkUnavailable : SessionValidationResult()
}

class AuthRepository {

    private val gson = Gson()

    suspend fun login(
        email: String,
        password: String,
        deviceLabel: String? = null
    ): Result<LoginResponse> {
        return try {
            val response = RetrofitClient.authApi.login(
                LoginRequest(
                    email = email,
                    password = password,
                    device_label = deviceLabel
                )
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.user != null && !body.token.isNullOrBlank()) {
                    Result.success(body)
                } else {
                    Result.failure(
                        Exception(body?.message ?: "Login failed. Missing session token or account details.")
                    )
                }
            } else {
                val serverMsg = parseErrorMessage(response.errorBody()?.string())
                val message = serverMsg ?: when (response.code()) {
                    400 -> "Email and password are required."
                    401 -> "Invalid email or password."
                    404 -> "Login API not found. Check the server URL."
                    else -> "Server error: ${response.code()}"
                }
                Result.failure(Exception(message))
            }
        } catch (_: Exception) {
            Result.failure(
                Exception("Unable to connect to the server. Check XAMPP and your network.")
            )
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        deviceLabel: String? = null
    ): Result<LoginResponse> {
        return try {
            val response = RetrofitClient.authApi.register(
                RegisterRequest(
                    name = name,
                    email = email,
                    password = password,
                    device_label = deviceLabel
                )
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.user != null && !body.token.isNullOrBlank()) {
                    Result.success(body)
                } else {
                    Result.failure(
                        Exception(body?.message ?: "Registration failed.")
                    )
                }
            } else {
                val serverMsg = parseErrorMessage(response.errorBody()?.string())
                val message = serverMsg ?: when (response.code()) {
                    400 -> "Please check your name, email, and password."
                    409 -> "An account with this email already exists."
                    404 -> "Registration API not found. Check the server URL."
                    else -> "Server error: ${response.code()}"
                }
                Result.failure(Exception(message))
            }
        } catch (_: Exception) {
            Result.failure(
                Exception("Unable to connect to the server. Check XAMPP and your network.")
            )
        }
    }

    suspend fun validateSession(): SessionValidationResult {
        return try {
            val response = RetrofitClient.authApi.validateSession()
            when {
                response.isSuccessful && response.body()?.success == true && response.body()?.user != null -> {
                    SessionValidationResult.Valid(response.body()!!.user!!)
                }
                response.code() == 401 || response.code() == 403 -> {
                    SessionValidationResult.InvalidOrExpired
                }
                else -> {
                    SessionValidationResult.NetworkUnavailable
                }
            }
        } catch (_: IOException) {
            SessionValidationResult.NetworkUnavailable
        } catch (_: Exception) {
            SessionValidationResult.NetworkUnavailable
        }
    }

    suspend fun logoutFromServer() {
        try {
            RetrofitClient.authApi.logout()
        } catch (_: Exception) {
            // Local session is always cleared even if device is offline
        }
    }

    private fun parseErrorMessage(rawError: String?): String? {
        if (rawError.isNullOrBlank()) return null
        return runCatching {
            gson.fromJson(rawError, BaseResponse::class.java)?.message?.takeIf { it.isNotBlank() }
        }.getOrNull()
    }
}
