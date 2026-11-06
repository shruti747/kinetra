package com.kinetra.app.data

data class UserProfile(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String? = null,
    val bio: String? = null,
    val location: String? = null,
    val preferred_sports: String? = null,
    val profile_photo: String? = null,
    val created_at: String? = null
)

data class ProfileResponse(
    val success: Boolean,
    val message: String? = null,
    val user: UserProfile? = null
)

data class ProfileUpdateRequest(
    val user_id: Int,
    val name: String,
    val email: String,
    val phone: String = "",
    val bio: String = "",
    val location: String = "London, UK",
    val preferred_sports: String = "Running · Cycling",
    val profile_photo: String = ""
)

