package com.kinetra.app.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class LoginRequest(
    val email: String,
    val password: String,
    val device_label: String? = null
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val device_label: String? = null
)

interface AuthApi {
    @POST("login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("register.php")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<LoginResponse>

    @GET("session_validate.php")
    suspend fun validateSession(): Response<ProfileResponse>

    @POST("logout.php")
    suspend fun logout(): Response<BaseResponse>
}
