package com.kinetra.app.data

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: User? = null
)

data class User(
    val id: Int,
    val name: String,
    val email: String
)