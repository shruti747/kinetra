package com.kinetra.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages active athlete session and local persistence across app navigation.
 */
object UserSession {
    private const val PREFS_NAME = "kinetra_user_prefs"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_PHONE = "user_phone"
    private const val KEY_USER_BIO = "user_bio"
    private const val KEY_USER_LOCATION = "user_location"
    private const val KEY_PREFERRED_SPORTS = "preferred_sports"
    private const val KEY_PROFILE_PHOTO = "profile_photo"

    // Default to active athlete ID 2 from kinetra_db
    var currentUserId: Int = 2
        private set

    private val _userProfile = MutableStateFlow(
        UserProfile(
            id = 2,
            name = "SHRUTI S",
            email = "252514022.sclas@saveetha.com",
            phone = "+91 98765 43210",
            bio = "Endurance runner & marathon athlete",
            location = "London, UK",
            preferred_sports = "Running · Cycling",
            profile_photo = ""
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val id = prefs.getInt(KEY_USER_ID, 2)
        currentUserId = id

        val name = prefs.getString(KEY_USER_NAME, "SHRUTI S") ?: "SHRUTI S"
        val email = prefs.getString(KEY_USER_EMAIL, "252514022.sclas@saveetha.com") ?: "252514022.sclas@saveetha.com"
        val phone = prefs.getString(KEY_USER_PHONE, "+91 98765 43210") ?: ""
        val bio = prefs.getString(KEY_USER_BIO, "Endurance runner & marathon athlete") ?: ""
        val location = prefs.getString(KEY_USER_LOCATION, "London, UK") ?: "London, UK"
        val sports = prefs.getString(KEY_PREFERRED_SPORTS, "Running · Cycling") ?: "Running · Cycling"
        val photo = prefs.getString(KEY_PROFILE_PHOTO, "") ?: ""

        _userProfile.value = UserProfile(
            id = id,
            name = name,
            email = email,
            phone = phone,
            bio = bio,
            location = location,
            preferred_sports = sports,
            profile_photo = photo
        )
    }

    fun updateProfile(context: Context?, profile: UserProfile) {
        currentUserId = profile.id
        _userProfile.value = profile

        context?.let {
            val prefs = it.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putInt(KEY_USER_ID, profile.id)
                .putString(KEY_USER_NAME, profile.name)
                .putString(KEY_USER_EMAIL, profile.email)
                .putString(KEY_USER_PHONE, profile.phone.orEmpty())
                .putString(KEY_USER_BIO, profile.bio.orEmpty())
                .putString(KEY_USER_LOCATION, profile.location ?: "London, UK")
                .putString(KEY_PREFERRED_SPORTS, profile.preferred_sports ?: "Running · Cycling")
                .putString(KEY_PROFILE_PHOTO, profile.profile_photo.orEmpty())
                .apply()
        }
    }
}

