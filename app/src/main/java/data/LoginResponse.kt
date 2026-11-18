package com.kinetra.app.data

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val expires_at: String? = null,
    val user: User? = null
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String? = null,
    val bio: String? = null,
    val location: String? = null,
    val preferred_sports: String? = null,
    val profile_photo: String? = null,
    val created_at: String? = null
) {
    fun toUserProfile(): UserProfile = UserProfile(
        id = id,
        name = name,
        email = email,
        phone = phone,
        bio = bio,
        location = location,
        preferred_sports = preferred_sports,
        profile_photo = profile_photo,
        created_at = created_at
    )
}
