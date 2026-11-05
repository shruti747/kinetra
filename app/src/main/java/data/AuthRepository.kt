package com.kinetra.app.data

class AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Result<LoginResponse> {
        return try {
            val response = RetrofitClient.authApi.login(
                LoginRequest(email, password)
            )

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null && body.success) {
                    Result.success(body)
                } else {
                    Result.failure(
                        Exception(body?.message ?: "Login failed.")
                    )
                }
            } else {
                val message = when (response.code()) {
                    401 -> "Invalid email or password."
                    404 -> "Login API not found. Check the server URL."
                    else -> "Server error: ${response.code()}"
                }

                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(
                Exception("Unable to connect to the server. Check XAMPP and your network.")
            )
        }
    }
}
